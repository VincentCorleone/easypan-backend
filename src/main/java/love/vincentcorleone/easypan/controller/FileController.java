package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.FileService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static love.vincentcorleone.easypan.exception.ResponseResult.success;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseResult<Object> upload(HttpSession session, @RequestParam("file") MultipartFile file){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        fileService.upload(nickName, file);
        return success("文件上传成功");
    }

    @GetMapping("/loadFiles")
    public ResponseResult<List<FileVo>> loadFiles(HttpSession session, @RequestParam("currentPath") String currentPath){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        List<FileVo> result = fileService.loadFiles(nickName, currentPath);
        return ResponseResult.success(result);
    }

    /**
     *
     * @param session
     * @param currentPath 以'/'开头和结尾
     * @param fileName
     * @return
     */
    @GetMapping("/createDownloadCode")
    public ResponseResult<Map<String, String>> createDownloadCode(HttpSession session, @RequestParam("currentPath") String currentPath, @RequestParam("fileName") String fileName){
        String nickName =  ((User)session.getAttribute(Constants.LOGIN_USER_KEY)).getNickName();
        String code = fileService.createDownloadCode(nickName, currentPath, fileName);
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        return ResponseResult.success(map);
    }

    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletResponse response, @RequestParam("code") String code){
        String filePath = fileService.downloadFile(code);
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);

        File file = null;
        FileInputStream is = null;


        try {
            response.setContentType("text/html;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"");
            file = new File(filePath);
            is = new FileInputStream(file);
            ServletOutputStream os = response.getOutputStream();
            IOUtils.copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("文件流关闭失败");
                }
            }
        }

    }

}
