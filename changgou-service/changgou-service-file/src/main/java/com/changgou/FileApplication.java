package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/1/28 16:51\
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableSwagger2
@EnableEurekaClient
public class FileApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(FileApplication.class,args);
    }
}
