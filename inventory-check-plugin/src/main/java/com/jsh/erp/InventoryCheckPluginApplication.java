package com.jsh.erp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * jshERP盘点业务插件主应用类
 * 
 * @author jshERP Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.jsh.erp.datasource.mappers")
public class InventoryCheckPluginApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InventoryCheckPluginApplication.class, args);
    }
}