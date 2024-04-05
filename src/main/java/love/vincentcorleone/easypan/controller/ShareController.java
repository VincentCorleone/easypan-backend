package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;
import love.vincentcorleone.easypan.entity.vo.ShareVoForGuest;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.ShareService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Resource
    private ShareService shareService;

    @Value("${front.host}")
    private String host;


    @GetMapping("/create")
    public ResponseResult<Object> create(HttpSession session, @RequestParam("currentPath")String currentPath, @RequestParam("fileName")String fileName, @RequestParam("validType")int validType){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        ShareVo shareVo = shareService.create(user,currentPath,fileName,validType);
        return ResponseResult.success(shareVo);
    }

    @GetMapping("/info")
    public ResponseResult<Object> info(@RequestParam("linkSuffix")String linkSuffix){
        ShareVoForGuest share = shareService.info(linkSuffix);
        return ResponseResult.success(share);
    }

    @GetMapping("/download")
    public void download(@RequestParam("suffix")String linkSuffix, HttpServletResponse response){
        HashMap<String,String> result = shareService.download(linkSuffix);




        File file = null;
        FileInputStream is = null;

        String fileName = result.get("fileName");
        String filePath = result.get("filePath");


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
