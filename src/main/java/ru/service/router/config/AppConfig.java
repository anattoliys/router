package ru.service.router.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class AppConfig {
    @Bean
    public ResourceBundleMessageSource messageSource() {

        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        source.setDefaultEncoding("utf-8");
        source.setDefaultLocale(Locale.US);

        return source;
    }
}
