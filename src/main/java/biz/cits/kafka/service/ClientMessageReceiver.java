package biz.cits.kafka.service;

import kafka.client.event.ClientMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

@Service
public class ClientMessageReceiver {

    private final KafkaReceiver<String, ClientMessage> receiver;

    @Value("clients2")
    String topicName;

    @Autowired
    public ClientMessageReceiver(KafkaReceiver<String, ClientMessage> kafkaReceiver) {
        this.receiver = kafkaReceiver;
    }


    public Flux<ConsumerRecord<String, ClientMessage>> receiveMessages() {
        return receiver.receiveAtmostOnce()
                .doOnNext(r -> System.out.println("Received message: %s \n"));
    }

}
