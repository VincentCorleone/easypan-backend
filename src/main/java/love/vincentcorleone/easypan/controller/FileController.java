package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseResult<Object> upload(HttpSession session, @RequestParam("file") MultipartFile file){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        fileService.upload(nickName, file);
        return ResponseResult.success("文件上传成功");
    }

    @GetMapping("/loadFiles")
    public ResponseResult<List<FileVo>> loadFiles(HttpSession session, @RequestParam("currentPath") String currentPath){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        List<FileVo> result = fileService.loadFiles(nickName, currentPath);
        return ResponseResult.success(result);
    }

}
