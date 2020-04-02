package fr.simplex_software.tests.kafka.consumer;

import lombok.extern.slf4j.*;
import org.springframework.cloud.stream.annotation.*;

@EnableBinding(TestKafkaChannel.class)
@Slf4j
public class KafkaConsumer
{
  @StreamListener("inboundTopic")
  public void doConsume(String msg)
  {
    log.debug ("### KafkaConsumer.doConsume(): We got a message \n\t{}", msg);
  }
}
