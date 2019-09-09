package com.perkins.mapper0;

import com.perkins.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper0 {

    @Select("select * from user")
    public List<User> listUser();

    @Insert("INSERT INTO user (name,age) VALUES (#{name},#{age})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public Integer save(User user);
}
