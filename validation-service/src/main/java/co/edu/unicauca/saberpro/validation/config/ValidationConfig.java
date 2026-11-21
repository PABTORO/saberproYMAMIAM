package co.edu.unicauca.saberpro.validation.config;

import co.edu.unicauca.saberpro.validation.domain.QuestionValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public QuestionValidator questionValidator() {
        return new QuestionValidator();
    }
}
