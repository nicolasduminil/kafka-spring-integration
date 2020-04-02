package fr.simplex_software.tests.kafka.producer;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cloud.stream.messaging.*;
import org.springframework.messaging.support.*;
import org.springframework.stereotype.*;

@Component
@Slf4j
public class KafkaProducer
{
  private Source source;

  @Autowired
  public KafkaProducer(Source source)
  {
    this.source = source;
  }

  public void publishKafkaMessage(String msg)
  {
    log.debug("### KafkaProducer.publisKafkaMessage(): Sending message to Kafka topic");
    source.output().send(MessageBuilder.withPayload(msg).build());
  }
}
