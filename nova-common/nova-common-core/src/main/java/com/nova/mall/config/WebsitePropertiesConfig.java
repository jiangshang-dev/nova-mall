package com.nova.mall.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WebsiteProperties.class)
public class WebsitePropertiesConfig {
}
