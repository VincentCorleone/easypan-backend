package love.vincentcorleone.easypan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.vincentcorleone.easypan.entity.po.Code2Path;
import org.apache.ibatis.annotations.Update;

public interface Code2PathMapper extends BaseMapper<Code2Path> {
    @Update("truncate table code2path")
    void deleteAll();
}
