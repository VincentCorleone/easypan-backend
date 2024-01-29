package love.vincentcorleone.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import love.vincentcorleone.easypan.entity.po.Code2Path;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.mapper.Code2PathMapper;
import love.vincentcorleone.easypan.service.FileService;
import love.vincentcorleone.easypan.util.AsyncTasks;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static love.vincentcorleone.easypan.util.DeleteDir.deleteDirectory;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private Code2PathMapper code2PathMapper;

    @Autowired
    private AsyncTasks asyncTasks;

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
    @Override
    public void upload(String nickName, String currentPath, MultipartFile file) {
        String projectPath = getProjectPath();
        String basePath = initUserRootDir(projectPath,nickName);
        String filePath = basePath + currentPath + file.getOriginalFilename();

        try {
            file.transferTo(new File(filePath));
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

    @Override
    public boolean uploadByChunks(String nickName, String currentPath, MultipartFile file, Integer chunkIndex, Integer chunks, String fileName) {
        String projectPath = this.getProjectPath();
        String basePath = initUserRootDir(projectPath,nickName);
        String filePath = basePath + currentPath + fileName;
        String fileTmpDirPath = filePath + "-tmp";

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
            asyncTasks.union(filePath,fileTmpDirPath,chunks);
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
}
