package com.github.spider.geek.dao.mapper;

import com.github.spider.geek.domain.UserDO;
import com.github.spider.geek.domain.example.UserDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

 /** create by system from table user(测试表)  */
public interface UserMapper {
    long countByExample(UserDOExample example);

    int deleteByExample(UserDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    List<UserDO> selectByExample(UserDOExample example);

    UserDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserDO record, @Param("example") UserDOExample example);

    int updateByExample(@Param("record") UserDO record, @Param("example") UserDOExample example);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    int batchInsertSelective(List<UserDO> records);

    int batchUpdateByPrimaryKeySelective(List<UserDO> records);

    int batchDeleteByPrimaryKey(List<Integer> list);

    int batchSelectByPrimaryKey(List<Integer> list);
}