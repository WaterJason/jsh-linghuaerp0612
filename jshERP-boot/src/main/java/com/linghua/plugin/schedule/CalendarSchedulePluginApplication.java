package com.linghua.plugin.schedule;

import com.gitee.starblues.annotation.Extract;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Calendar Schedule Plugin Main Application
 * 日历排班插件主应用类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@SpringBootApplication
@Extract(bus = "pluginBus")
public class CalendarSchedulePluginApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CalendarSchedulePluginApplication.class, args);
    }
}
