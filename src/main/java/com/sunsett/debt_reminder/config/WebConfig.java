package com.sunsett.debt_reminder.config;

import com.sunsett.debt_reminder.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilter() {
        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtRequestFilter);
        registrationBean.addUrlPatterns("/users/*", "/api/debts/*");
        return registrationBean;
    }
}
