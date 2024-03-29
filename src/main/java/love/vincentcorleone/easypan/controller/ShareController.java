package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;
import love.vincentcorleone.easypan.entity.vo.ShareVoForGuest;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.service.ShareService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
