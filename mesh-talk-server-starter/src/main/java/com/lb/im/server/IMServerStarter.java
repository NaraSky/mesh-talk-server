package com.lb.im.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = {"com.lb.im", "com.alibaba.cola"})
@SpringBootApplication
public class IMServerStarter {

    public static void main(String[] args) {
        SpringApplication.run(IMServerStarter.class, args);
    }
}
