package fr.simplex_software.tests.kafka.consumer;

import fr.simplex_software.tests.kafka.model.*;
import lombok.extern.slf4j.*;
import org.springframework.cloud.stream.annotation.*;
import org.springframework.cloud.stream.messaging.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.*;

@Component
@Slf4j
public class KafkaConsumer
{
  @StreamListener(Sink.INPUT)
  public void doConsume(@Payload GetAllDestinationsResponse msg)
  {
    log.debug ("### KafkaConsumer.doConsume(): We got a message \n\t{}", msg);
  }
}
