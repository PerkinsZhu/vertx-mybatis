package com.perkins.mapper3;

import com.perkins.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper3 {

    @Select("select * from user")
    public List<User> listUser();
}