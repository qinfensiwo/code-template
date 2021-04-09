package com.changgou.goods;

import com.changgou.entity.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/1/29 18:20\
 */
@EnableEurekaClient
@EnableSwagger2
@SpringBootApplication
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication
{
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }


    @Bean
    public IdWorker idWorker(){
        return new IdWorker(0,0);
    }
}
