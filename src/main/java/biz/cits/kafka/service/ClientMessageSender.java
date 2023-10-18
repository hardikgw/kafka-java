package biz.cits.kafka.service;

import kafka.client.event.ClientMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class ClientMessageSender {

    final KafkaProducer<String, ClientMessage> kafkaProducer;

    @Value("clients2")
    String topicName;

    @Autowired
    public ClientMessageSender(KafkaProducer<String, ClientMessage> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendMessages(ArrayList<ClientMessage> clientMessages) {
        for (ClientMessage orderEvent : clientMessages) {
            ProducerRecord<String, ClientMessage> record = new ProducerRecord<>(topicName, orderEvent);
            kafkaProducer.send(record);
            kafkaProducer.flush();
            System.out.println("sent " + record);
        }
    }

}
