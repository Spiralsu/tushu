package com.booksharing.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 【终极方案】：直接映射到你的物理绝对路径
        String absolutePath = "file:E:/java/project/校园旧书漂流共享系统/代码/book-backend/src/main/resources/static/files/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations(absolutePath);
    }
}
