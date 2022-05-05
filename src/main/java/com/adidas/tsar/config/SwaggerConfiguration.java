package com.adidas.tsar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@PropertySource("classpath:application.properties")
public class SwaggerConfiguration {

    private static final String BASE_PACKAGE = "com.adidas.tsar";
    private static final String CONTACT_NAME = "Pavel Rusanov";
    private static final String CONTACT_EMAIL = "pavel.rusanov@adidas.com";

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("TSAR Planogram API")
            .description("TSAR Planogram API")
            .version("1.0-SNAPSHOT")
            .contact(new Contact(CONTACT_NAME, null, CONTACT_EMAIL))
            .build();
    }

}
