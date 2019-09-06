package com.perkins;


import com.perkins.services.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
        ApplicationContext appContext = new AnnotationConfigApplicationContext("com.perkins");
        UserService userService = appContext.getBean(UserService.class);
        userService.show();
    }

}