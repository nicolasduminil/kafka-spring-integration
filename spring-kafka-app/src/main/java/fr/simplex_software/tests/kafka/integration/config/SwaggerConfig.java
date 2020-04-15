package fr.simplex_software.tests.kafka.integration.config;

import org.springframework.context.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;

import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig
{
  public ApiInfo apiInfo()
  {
    return new ApiInfoBuilder().title("Kafka - Spring Cloud Integration Demo")
      .description("Rest API for demonstrating Spring Cloud Stream with Kafka.")
      .contact(new Contact("Nicolas DUMINIL", "http://www.simplex-software.fr", "nicolas.duminil@simplex-software.fr"))
      .license("Copyright © Simplex Software 2020 - All Rights Reserved")
      .licenseUrl("https://www.simplex-software.fr")
      .version("1.0 Covid-19 Era (April 2020)")
      .build();
  }

  @Bean
  public Docket configureControllerPackageAndConvertors()
  {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .paths(PathSelectors.any())
      .apis(RequestHandlerSelectors.basePackage("fr.simplex_software.tests.kafka.integration.controllers.impl"))
      .build()
      .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
      .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class)
      .apiInfo(apiInfo());
  }
}
