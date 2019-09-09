package com.perkins.mapper2;

import com.perkins.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import java.util.List;

public interface User2Mapper {
    public List<User> listUser();

    @Insert("INSERT INTO user (name,age) VALUES (#{name},#{age})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public Integer save(User user);
}
