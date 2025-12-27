package com.example.foodbe.config;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        // ReloadableResourceBundleMessageSource: cho phép reload file và multi-locale
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        // Base name: Spring sẽ tìm messages.properties, messages_vi.properties,...
        messageSource.setBasename("classpath:messages");

        // UTF-8 để đọc tiếng Việt, ký tự đặc biệt
        messageSource.setDefaultEncoding("UTF-8");

        // Cache file trong 3600 giây (1 giờ). 0 = reload mỗi lần (chỉ dev)
        messageSource.setCacheSeconds(3600);

        // Optional: nếu key không tìm thấy, fallback sang key chính nó
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    // cấu hình để herbinate validator có thể sử dụn mesage properties để tìm mesage
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

}
