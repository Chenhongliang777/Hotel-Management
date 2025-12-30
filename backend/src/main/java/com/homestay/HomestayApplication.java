package com.homestay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.homestay.module.*.mapper")
@EnableScheduling
public class HomestayApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomestayApplication.class, args);
        System.out.println("============================================");
        System.out.println("    民宿业务管理系统启动成功!");
        System.out.println("    接口文档: http://localhost:8080/api/swagger-ui.html");
        System.out.println("============================================");
    }
}
