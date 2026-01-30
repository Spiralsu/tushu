package com.shanzhu.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地文件目录，确保图片可以访问
        // 请确保 System.getProperty("user.dir") + "/src/main/resources/static/files/" 路径下确实有图片
        registry.addResourceHandler("/files/**")
                .addResourceLocations("classpath:/static/files/")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/src/main/resources/static/files/");
    }
}