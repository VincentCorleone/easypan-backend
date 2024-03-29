package love.vincentcorleone.easypan.service.impl;

import love.vincentcorleone.easypan.entity.po.Share;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;
import love.vincentcorleone.easypan.mapper.ShareMapper;
import love.vincentcorleone.easypan.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    @Override
    public ShareVo create(User user, String currentPath, String fileName, int validType, int howCode, String code) {
        String link = generateRandomString(true);
        Share share = new Share();
        share.setLinkSuffix(link);
        share.setUserId(user.getId());
        Date currentTime = new Date();
        share.setShareTime(currentTime);
        share.setViewDir(currentPath);
        share.setFileName(fileName);
        share.setForever(false);

        switch (validType){
            case 1:
                share.setExpireTime(plusDay(currentTime,1));
                break;
            case 2:
                share.setExpireTime(plusDay(currentTime,3));
                break;
            case 3:
                share.setExpireTime(plusDay(currentTime,7));
                break;
            case 4:
                share.setForever(true);
                break;
        }

        if(howCode == 1){
            share.setCode(code);
        }else if(howCode == 2){
            share.setCode(generateRandomString(false));
        }

        share.setViewTimes(0);
        try {
            shareMapper.insert(share);
            return new ShareVo(share);
        } catch (DuplicateKeyException e){
            throw new RuntimeException("此文件已分享过，不能重复分享");
        }

    }

    private Date plusDay(Date from,int dayOffset){
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }


    private String generateRandomString(boolean isLink){
        // 创建一个安全的随机数生成器
        SecureRandom random = new SecureRandom();

        //20x8=160bit
        //20位长度的base64 2的6次方=64  20*6=120bit
        byte[] randomBytes = new byte[15];
        random.nextBytes(randomBytes);
        String base64String = Base64.getEncoder().encodeToString(randomBytes);


        if(isLink){
            return base64String;
        }else{
            return base64String.substring(0,5);
        }
    }
}
