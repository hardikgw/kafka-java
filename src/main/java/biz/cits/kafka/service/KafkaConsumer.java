package biz.cits.kafka.service;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.sound.midi.SysexMessage;
import java.io.IOException;

@Service
public class KafkaConsumer {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @KafkaListener(topics = "${kafka.topic}", groupId = "group_id")
    public void consume(String message) throws IOException {
        System.out.println(message);
        logger.info(String.format("#### -> Consumed message -> %s", message));
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> process() {
        return input ->
                input.foreach((key, value) -> {
                    System.out.println("Key: " + key + " Value: " + value);
                });
    }
}
