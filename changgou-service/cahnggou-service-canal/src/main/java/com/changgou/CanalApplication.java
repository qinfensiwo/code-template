package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/4/10 0:44
 */
@EnableFeignClients(basePackages = {"com.changgou.content.feign"})
@EnableEurekaClient
@EnableCanalClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CanalApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CanalApplication.class,args);
    }
}
