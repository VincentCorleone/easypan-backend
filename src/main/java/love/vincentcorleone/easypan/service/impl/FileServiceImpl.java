package love.vincentcorleone.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import love.vincentcorleone.easypan.entity.po.Code2Path;
import love.vincentcorleone.easypan.entity.po.LargeFile;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.mapper.Code2PathMapper;
import love.vincentcorleone.easypan.mapper.LargeFileMapper;
import love.vincentcorleone.easypan.service.FileService;
import love.vincentcorleone.easypan.service.UserService;
import love.vincentcorleone.easypan.util.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static love.vincentcorleone.easypan.util.FileUtils.deleteDirectory;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private Code2PathMapper code2PathMapper;

    @Autowired
    private LargeFileMapper largeFileMapper;

    @Resource
    private UserService userService;

    private int testTransaction = 0;

    private String getProjectPath(){
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        URL url = resourceLoader.getClassLoader().getResource("");
        String projectPath = null;
        try {
            projectPath = url.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return projectPath;
    }

    private String initUserRootDir(String projectPath, String nickName){
        String basePath = projectPath + "files/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + nickName  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }

    private String initPublicFileDir(String projectPath){
        String basePath = projectPath + "files/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + "public/"  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }
    @Override
    public void upload(String nickName, String currentPath, MultipartFile file) {
        String projectPath = getProjectPath();
        String basePath = initUserRootDir(projectPath,nickName);
        String filePath = basePath + currentPath + file.getOriginalFilename();

        File toFile = new File(filePath);
        if(toFile.exists()){
            throw new RuntimeException("该目录下存在相同文件名的文件，无法上传");
        }

        try {
            file.transferTo(toFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<FileVo> loadFiles(String nickName, String currentPath) {
        String basePath = initUserRootDir(getProjectPath(),nickName);
        String dirPath = basePath + currentPath;
        File dir = new File(dirPath);
        if(dir.listFiles() == null){
            return new ArrayList<>();
        }else {
            List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
            return files.stream().map(FileVo::new).collect(Collectors.toList());
        }
    }

    @Override
    public String createDownloadCode(String nickName, String currentPath, String fileName) {
        String basePath = initUserRootDir(getProjectPath(),nickName);
        String filePath = basePath + currentPath + fileName;

        String code = RandomStringUtils.random(10,true,true);

        Code2Path code2Path = new Code2Path();
        code2Path.setCode(code);
        code2Path.setPath(filePath);

        code2PathMapper.insert(code2Path);
        return code;
    }

    @Override
    public String downloadFile(String code) {
        QueryWrapper<Code2Path> wrapper = new QueryWrapper<Code2Path>().eq("code", code);
        Code2Path code2Path = code2PathMapper.selectOne(wrapper);
        code2PathMapper.delete(wrapper);
        return code2Path.getPath();
    }

    boolean checkExistsSameFile(){
        return false;
    }


    private void checkSameName(String filePath){
        //检查同目录下同名文件
        //todo 重名逻辑要改
        if(new File(filePath).exists()){
            throw new RuntimeException("该目录下存在相同文件名的文件，无法上传");
        }

    }
    public boolean checkMd5(User user, String md5, String currentPath, String fileName){
        String projectPath = this.getProjectPath();
        String basePath = initUserRootDir(projectPath,user.getNickName());
        String filePath = basePath + currentPath + fileName;
        String fileTmpDirPath = filePath + "-tmp";

        checkSameName(filePath);
        //无同名文件，检查是否进行秒传逻辑

        QueryWrapper<LargeFile> pqw = new QueryWrapper<LargeFile>().eq("md5",md5);
        LargeFile largeFile = largeFileMapper.selectOne(pqw);

        if(largeFile !=null){
            //private文件中有和上传文件MD5相同的记录
            if(!largeFile.isPublic()) {
                //移动文件到公共文件目录
                Long ownerUserId = largeFile.getUserId();
                String ownerUserNickName = userService.findUserById(ownerUserId).getNickName();
                String ownerBasePath = initUserRootDir(projectPath,ownerUserNickName);
                FileUtils.moveFile(largeFile.getDiskPath(ownerBasePath), initPublicFileDir(getProjectPath()) + md5);

                //旧私有文件记录改成公共文件记录
                largeFile.setPublic(true);
                largeFileMapper.updateById(largeFile);
            }
            //public文件中有和上传文件MD5相同的记录

            //新建公共文件记录
            LargeFile newLargeFile = new LargeFile();
            newLargeFile.setMd5(largeFile.getMd5());
            newLargeFile.setPublic(true);
            newLargeFile.setViewDir(currentPath);
            newLargeFile.setFileName(fileName);
            newLargeFile.setUserId(user.getId());
            largeFileMapper.insert(newLargeFile);
            return true;
        }

        return false;

    }
    @Override
    public boolean uploadByChunks(User user, String currentPath, MultipartFile file, Integer chunkIndex, Integer chunks, String fileName, String md5) {
        String projectPath = this.getProjectPath();
        String basePath = initUserRootDir(projectPath,user.getNickName());
        String filePath = basePath + currentPath + fileName;
        String fileTmpDirPath = filePath + "-tmp";

        checkSameName(filePath);





        File tmpDir = new File(fileTmpDirPath);
        if (!tmpDir.exists()){
            tmpDir.mkdir();
        }

        try {
            file.transferTo(new File(fileTmpDirPath + "/" + chunkIndex));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (chunkIndex == chunks - 1){
            //新建私有文件记录
            LargeFile newLargeFile = new LargeFile();
            newLargeFile.setMd5(md5);
            newLargeFile.setPublic(false);
            newLargeFile.setViewDir(currentPath);
            newLargeFile.setFileName(fileName);
            newLargeFile.setUserId(user.getId());

            this.unionAndInsert(filePath,fileTmpDirPath,chunks,newLargeFile);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void newFolder(String nickName, String currentPath, String folderName) {
        String basePath = initUserRootDir(getProjectPath(),nickName);
        String dirPath = basePath + currentPath + folderName;

        File dir = new File(dirPath);
        dir.mkdir();
    }


    private void unionAndInsert(String toPath, String fromPathDir, Integer chunks, LargeFile newLargeFile){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(toPath, "rw");

            byte[] buffer = new byte[1024 * 10];

            for (int i = 0; i < chunks; i++) {
                int len = -1;
                RandomAccessFile from = null;
                try {
                    from = new RandomAccessFile(fromPathDir + "/" + i, "r");
                    while((len = from.read(buffer)) != -1){
                        raf.write(buffer,0,len);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("文件访问失败");
                } catch (IOException e) {
                    throw new RuntimeException("合并文件失败");
                } finally {
                    from.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("合并文件失败");
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                throw new RuntimeException("关闭写入的文件失败");
            }
            Path directoryPath = Paths.get(fromPathDir); //替换为具体的目录路径
            try {
                deleteDirectory(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException("上传文件后删除文件夹失败");
            }
            largeFileMapper.insert(newLargeFile);
        }

    }
}
