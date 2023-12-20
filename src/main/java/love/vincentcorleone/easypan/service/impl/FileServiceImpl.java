package love.vincentcorleone.easypan.service.impl;

import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.service.FileService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Service
public class FileServiceImpl implements FileService {

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
    @Override
    public void upload(String nickName, MultipartFile file) {
        String projectPath = getProjectPath();

        String basePath = projectPath + "files/";
        File dir = new File(basePath);
//         判断当前目录是否存在
        if (!dir.exists()){
//          不存在的时候进行创建
            dir.mkdirs();
        }
        basePath = basePath + nickName  + "/" ;
        dir = new File(basePath);
//         判断当前目录是否存在
        if (!dir.exists()){
//          不存在的时候进行创建
            dir.mkdirs();
        }

        String finalPath = basePath  + file.getOriginalFilename();

//      转存临时文件到指定的位置  参数是一个URL路径
        try {
            file.transferTo(new File(finalPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
