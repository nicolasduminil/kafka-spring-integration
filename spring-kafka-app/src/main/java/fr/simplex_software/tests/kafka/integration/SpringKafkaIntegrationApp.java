package fr.simplex_software.tests.kafka.integration;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.stream.annotation.*;
import org.springframework.cloud.stream.messaging.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
@ComponentScan(basePackages = {"fr.simplex_software.tests.kafka"})
@EnableBinding({Source.class, Sink.class})
public class SpringKafkaIntegrationApp
{
  public static void main(String[] args)
  {
    SpringApplication.run(SpringKafkaIntegrationApp.class, args);
  }
}
