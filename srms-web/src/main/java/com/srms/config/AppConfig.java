package com.srms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Root Spring application context (loaded by ContextLoaderListener in web.xml).
 * Scans the service and DAO layers; controllers are scanned by {@link WebConfig}.
 */
@Configuration
@Import(HibernateConfig.class)
@ComponentScan(basePackages = {"com.srms.dao", "com.srms.service", "com.srms.util"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
                classes = {Controller.class, ControllerAdvice.class}))
public class AppConfig {
}
