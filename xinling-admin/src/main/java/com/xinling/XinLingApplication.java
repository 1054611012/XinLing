package com.xinling;

import com.xinling.framework.config.MyBatisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author ruo
 */
@SpringBootApplication
@EnableScheduling // 启用定时任务
public class XinLingApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        System.out.println(MyBatisConfig.class.getClassLoader().getResource("mybatis/mybatis-config.xml"));
        SpringApplication.run(XinLingApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  心灵启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
