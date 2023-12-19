package love.vincentcorleone.easypan.controller;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file){
        System.out.println(file);
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        URL url = resourceLoader.getClassLoader().getResource("");
        String projectPath = null;
        try {
            projectPath = url.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String basePath = projectPath + "files/";
        String finalPath = basePath + file.getOriginalFilename();


//      2. 判断转存的目录是否存在
//         File既可以代表一个目录，又可以代表一个文件
        File dir = new File(basePath);
//         判断当前目录是否存在
        if (!dir.exists()){
//          不存在的时候进行创建
            dir.mkdirs();
        }

//      转存临时文件到指定的位置  参数是一个URL路径
        try {
            file.transferTo(new File(finalPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
