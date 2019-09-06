package com.perkins.services;

import com.perkins.config.BaseMybatisConfig;
import com.perkins.config.SpecialDataSourceConfig;
import com.perkins.entity.User;
import com.perkins.mapper.UserMapper;
import com.perkins.mapperdb1.UserMapper1;
import com.perkins.servvice.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import vts.vertxbeans.rxjava.VertxBeans;
import vts.vertxmybatis.MybatisConfiguration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Service("userService")
@Import({VertxBeans.class, BaseMybatisConfig.class, SpecialDataSourceConfig.class})
public class UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserMapper1 userMapper1;

    @Autowired
    BookService bookService1;

    public void show() {
        List<User> list = userMapper.listUser();
        System.out.println(list.size() + "---");
        list.forEach(u -> {
            System.out.println(u.toString());
        });

        List<User> list2 = userMapper1.listUser();
        System.out.println(list2.size() + "---");
        list2.forEach(u -> {
            System.out.println(u.toString());
        });

        System.out.println(bookService1.name);

//        bookService1.userService.show();
    }
}
