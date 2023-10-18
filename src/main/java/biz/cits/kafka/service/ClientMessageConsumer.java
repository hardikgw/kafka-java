package biz.cits.kafka.service;

import kafka.client.event.ClientMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Collections;

@Service
public class ClientMessageConsumer {

    final KafkaConsumer<String, ClientMessage> kafkaConsumer;

    @Value("clients2")
    String topicName;

    @Autowired
    public ClientMessageConsumer(KafkaConsumer<String, ClientMessage> kafkaConsumer) {
        this.kafkaConsumer = kafkaConsumer;
    }


    public Flux<ReceiverRecord<String, String>> receiveMessages() {
        this.kafkaConsumer.subscribe(Collections.singletonList(topicName));
        // Continually poll Kafka for new messages, and print each OrderEvent received
        try {
            while (true) {
                ConsumerRecords<String, ClientMessage> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, ClientMessage> record : records) {
                    System.out.println(record.value());
                }
            }
        } finally {
            kafkaConsumer.close();
        }
    }

}
