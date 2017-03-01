/*
 * Copyright (c) 2016. com.biqasoft
 */

package com.biqasoft.exporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(value = "com.biqasoft", excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.biqasoft.entity")})
@Configuration
@EnableAutoConfiguration(exclude = {MongoDataAutoConfiguration.class, MongoAutoConfiguration.class,
        SecurityAutoConfiguration.class})
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

}
