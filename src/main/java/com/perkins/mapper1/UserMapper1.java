package com.perkins.mapper1;

import com.perkins.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper1 {

    @Select("select * from user")
    public List<User> listUser();
}
