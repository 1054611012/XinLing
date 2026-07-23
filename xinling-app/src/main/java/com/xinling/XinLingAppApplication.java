package com.xinling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * APP端服务启动入口
 * <p>
 * 独立部署的移动端 API 服务，端口 8081。
 * 提供用户注册登录、专注、睡眠、音频、VIP、社区等移动端接口。
 */
@SpringBootApplication
public class XinLingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinLingAppApplication.class, args);
        System.out.println("❤️ 心灵 APP 服务启动成功 ❤️");
    }
}
