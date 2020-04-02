package fr.simplex_software.tests.kafka.consumer;

import org.springframework.cloud.stream.annotation.*;
import org.springframework.messaging.*;

public interface TestKafkaChannel
{
  @Input("inboundChannel")
  public SubscribableChannel doSubscribe();
}
