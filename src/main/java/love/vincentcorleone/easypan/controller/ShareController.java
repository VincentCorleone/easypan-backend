package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.ShareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Resource
    private ShareService shareService;

    @GetMapping("/create")
    public ResponseResult<Object> create(HttpServletRequest request, HttpSession session, @RequestParam("currentPath")String currentPath, @RequestParam("fileName")String fileName, @RequestParam("validType")int validType, @RequestParam("howCode")int howCode, @RequestParam(value = "code",required = false)String code){
        User user =  (User)session.getAttribute(Constants.LOGIN_USER_KEY);
        ShareVo shareVo = shareService.create(user,currentPath,fileName,validType,howCode,code);
        String uri = request.getRequestURL().toString();
        String api = "/api/share/";
        String link = uri.substring(0,uri.indexOf(api) + api.length()) + shareVo.getLinkSuffix();
        shareVo.setLink(link);
        return ResponseResult.success(shareVo);
    }


}
