package app.narvi.protego.spring.rules;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("app.narvi")
@PropertySource("classpath:application.properties")
public class TestSpringConfiguration {
}
