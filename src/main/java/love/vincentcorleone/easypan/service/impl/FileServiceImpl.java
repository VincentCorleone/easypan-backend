package love.vincentcorleone.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.vincentcorleone.easypan.entity.po.Code2Path;
import love.vincentcorleone.easypan.entity.po.LargeFile;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.mapper.Code2PathMapper;
import love.vincentcorleone.easypan.mapper.LargeFileMapper;
import love.vincentcorleone.easypan.service.FileService;
import love.vincentcorleone.easypan.util.FfmpegUtils;
import love.vincentcorleone.easypan.util.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static love.vincentcorleone.easypan.util.FileUtils.*;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private Code2PathMapper code2PathMapper;

    @Autowired
    private LargeFileMapper largeFileMapper;

    @Autowired
    private FfmpegUtils ffmpegUtils;

    @Override
    public void upload(User user, String currentPath, MultipartFile file) {
        String basePath = initUserRootDir(user.getNickName());
        String filePath = basePath + currentPath + file.getOriginalFilename();

        File toFile = new File(filePath);
        checkExistsSameFile(user, currentPath,file.getOriginalFilename());

        try {
            file.transferTo(toFile);
            ffmpegUtils.afterUpload(Objects.requireNonNull(file.getOriginalFilename()),currentPath,user.getNickName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<FileVo> loadFiles(User user, String currentPath) {
        String basePath = initUserRootDir(user.getNickName());
        String dirPath = basePath + currentPath;
        File dir = new File(dirPath);
        if(dir.listFiles() == null){
            return new ArrayList<>();
        }else {
            List<FileVo> smallFiles = Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(file->file.length() < 1024 * 1024 * 10 ).map(FileVo::new).collect(Collectors.toList());
            List<FileVo> result = new ArrayList<>(smallFiles);

            QueryWrapper<LargeFile> qw = new QueryWrapper<LargeFile>().eq("user_id",user.getId()).eq("view_dir",currentPath);
            List<FileVo> largeFiles = largeFileMapper.selectList(qw).stream().map(FileVo::new).collect(Collectors.toList());
            result.addAll(largeFiles);
            return result;
        }
    }


    @Override
    public String createDownloadCode(User user, String currentPath, String fileName) {
        String basePath = initUserRootDir(user.getNickName());
        String filePath = basePath + currentPath + fileName;

        File file = new File(filePath);
        LargeFile largeFile = getLargeFileBy3(user,currentPath,fileName);

        Code2Path code2Path = new Code2Path();

        String finalPath = null;
        if(file.exists()){
            //要下载的文件是私有的且小于10m
            //要下载的文件是私有的且大于10m
            finalPath = filePath;
        }else if(largeFile!=null && largeFile.isPublic()){
            //要下载的文件是公有的
            finalPath = largeFile.getDiskPath();
            code2Path.setFileName(fileName);
        }else{
            //异常
            throw new RuntimeException("要下载的文件不存在");
        }




        String code = RandomStringUtils.random(10,true,true);


        code2Path.setCode(code);
        code2Path.setPath(finalPath);

        code2PathMapper.insert(code2Path);
        return code;
    }

    @Override
    public Code2Path downloadFile(String code) {
        QueryWrapper<Code2Path> wrapper = new QueryWrapper<Code2Path>().eq("code", code);
        Code2Path code2Path = code2PathMapper.selectOne(wrapper);
        code2PathMapper.delete(wrapper);
        return code2Path;
    }

    public LargeFile getLargeFileBy3(User user, String currentPath, String fileName){
        QueryWrapper<LargeFile> qw = new QueryWrapper<LargeFile>()
                .eq("user_id",user.getId())
                .eq("view_dir",currentPath)
                .eq("file_name",fileName);
        return largeFileMapper.selectOne(qw);
    }

    @Override
    public void delete(User user, String currentPath, String fileName) {
        String finalPath = null;
        String attachmentFinalPath = null;

        String basePath = initUserRootDir(user.getNickName());
        String absoluteFilePath = basePath + currentPath + fileName;

        File file = new File(absoluteFilePath);
        LargeFile largeFile = this.getLargeFileBy3(user, currentPath, fileName);

        boolean delete = true;

        if (file.exists()) {
            //私有小文件

            finalPath = absoluteFilePath;
            attachmentFinalPath = initUserAttachmentDir(user.getNickName()) + currentPath + fileName;

            if (largeFile!=null){
                //私有大文件
                largeFileMapper.deleteById(largeFile);
            }
        }  else if (largeFile != null && largeFile.isPublic()) {
            //公有文件
            finalPath = largeFile.getDiskPath();
            attachmentFinalPath = largeFile.getAttachmentDiskPath();

            largeFileMapper.deleteById(largeFile);
            QueryWrapper<LargeFile> qw = new QueryWrapper<LargeFile>().eq("md5",largeFile.getMd5());
            delete = largeFileMapper.selectList(qw).size() == 0;
        } else {
            //异常
            throw new RuntimeException("要删除的文件不存在");
        }

        if(delete){
            if(new File(finalPath).isDirectory()){
                QueryWrapper<LargeFile> qw = new QueryWrapper<LargeFile>().likeRight("view_dir",currentPath).eq("user_id",user.getId());
                List<LargeFile> largeFiles = largeFileMapper.selectList(qw);
                for (LargeFile lf: largeFiles) {
                    FileUtils.delete(new File(lf.getDiskPath()));
                    FileUtils.delete(new File(lf.getAttachmentDiskPath()));
                }
                largeFileMapper.delete(qw);
            }
            FileUtils.delete(new File(finalPath));
            FileUtils.delete(new File(attachmentFinalPath));
        }
    }

    @Override
    public void rename(User user, String currentPath, String fileName, String newName) {
        String basePath = initUserRootDir(user.getNickName());
        String filePath = basePath + currentPath + fileName;
        //分情况
        if(new File(filePath).isDirectory()){
            //1.是文件夹
            new File(filePath).renameTo(new File(basePath + currentPath + newName));
            QueryWrapper<LargeFile> qw = new QueryWrapper<LargeFile>().eq("user_id",user.getId()).likeRight("view_dir",currentPath + fileName + "/");
            List<LargeFile> largeFiles = largeFileMapper.selectList(qw);
            for (LargeFile lf: largeFiles) {
                String view_dir = lf.getViewDir();
                String new_view_dir = currentPath + newName + view_dir.substring(currentPath.length()+fileName.length());
                lf.setViewDir(new_view_dir);
                largeFileMapper.updateById(lf);
            }

        }else{
            //2.是文件
            LargeFile largeFile = getLargeFileBy3(user,currentPath,fileName);
            if(new File(filePath).exists()){
                //2.1 是私有文件
                new File(filePath).renameTo(new File(basePath + currentPath + newName));
                String attachmentPath = initUserAttachmentDir(user.getNickName()) + currentPath + fileName;
                File attachmentFile = new File(attachmentPath);
                if(attachmentFile.exists()){
                    attachmentFile.renameTo(new File(initUserAttachmentDir(user.getNickName())+ currentPath + newName));
                }
                if(largeFile == null){
                    //2.1.1 是私有小文件

                } else{
                    //2.1.2 是私有大文件
                    largeFile.setFileName(newName);
                    largeFileMapper.updateById(largeFile);
                }
            } else if (largeFile.isPublic()) {
                //2.2 是公有文件
                largeFile.setFileName(newName);
                largeFileMapper.updateById(largeFile);
            } else{
                //2.3 异常
                throw new RuntimeException("找不到要重命名的文件");
            }
        }
    }


    private void checkExistsSameFile(User user,String currentPath, String fileName){
        //检查同目录下同名文件
        String basePath = initUserRootDir(user.getNickName());
        String filePath = basePath + currentPath + fileName;

        if(new File(filePath).exists()){
            throw new RuntimeException("该目录下存在相同文件名的文件，无法上传");
        }

        LargeFile largeFile = getLargeFileBy3(user,currentPath,fileName);
        if(largeFile !=null ){
            throw new RuntimeException("该目录下存在相同文件名的文件，无法上传");
        }
    }
    public boolean checkMd5(User user, String md5, String currentPath, String fileName){



        checkExistsSameFile(user,currentPath,fileName);
        //无同名文件，检查是否进行秒传逻辑

        QueryWrapper<LargeFile> pqw = new QueryWrapper<LargeFile>().eq("md5",md5);
        LargeFile largeFile = largeFileMapper.selectOne(pqw);

        if(largeFile !=null){
            //private文件中有和上传文件MD5相同的记录
            if(!largeFile.isPublic()) {
                //移动文件到公共文件目录
                FileUtils.moveFile(largeFile.getDiskPath(), initPublicFileDir() + md5);

                //移动附属文件到公共目录
                if(new File(largeFile.getAttachmentDiskPath()).exists()){
                    FileUtils.moveFile(largeFile.getAttachmentDiskPath(),initAttachmentPublicFileDir() + md5);
                }

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
        String basePath = initUserRootDir(user.getNickName());
        String filePath = basePath + currentPath + fileName;
        String fileTmpDirPath = filePath + "-tmp";

        checkExistsSameFile(user,currentPath,fileName);


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
            ffmpegUtils.afterUpload(fileName,currentPath,user.getNickName());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void newFolder(String nickName, String currentPath, String folderName) {
        String basePath = initUserRootDir(nickName);
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
