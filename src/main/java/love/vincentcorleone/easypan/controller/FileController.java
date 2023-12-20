package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.service.FileService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(HttpSession session, @RequestParam("file") MultipartFile file){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        fileService.upload(nickName, file);
        Map<String,String> result = new HashMap<>();
        result.put("message","文件上传成功");
        return new ResponseEntity<Object>(result, HttpStatusCode.valueOf(200));
    }
}
