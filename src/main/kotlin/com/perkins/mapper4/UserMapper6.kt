package com.perkins.mapper4

import com.perkins.entity.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

@Mapper
interface UserMapper6 {
    @Select("select * from user")
    fun listUser(): List<User>
}