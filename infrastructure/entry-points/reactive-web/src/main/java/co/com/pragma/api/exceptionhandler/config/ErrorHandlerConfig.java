package co.com.pragma.api.exceptionhandler.config;

import co.com.pragma.api.exceptionhandler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class ErrorHandlerConfig implements WebFluxConfigurer {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ErrorAttributes errorAttributes,
                                                         WebProperties webProperties,
                                                         ApplicationContext applicationContext,
                                                         ServerCodecConfigurer serverCodecConfigurer) {
        GlobalExceptionHandler handler = new GlobalExceptionHandler(errorAttributes, webProperties.getResources(), applicationContext);
        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }

}