package biz.cits.kafka;

import kafka.client.event.ClientMessage;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

@SpringBootApplication
public class App {

    @Value("clients2")
    String topicName;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    public Properties getProperties(String propertyFileName) {
        Resource resource = new ClassPathResource(propertyFileName);
        Properties props = new Properties();
        try {
            props.load(new FileReader(resource.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    @Bean
    public KafkaProducer<String, ClientMessage> kafkaProducer() {
        return new KafkaProducer<>(getProperties("producer.properties"));
    }

    @Bean
    public KafkaConsumer<String, ClientMessage> kafkaConsumer() {
        return new KafkaConsumer<>(getProperties("consumer.properties"));
    }

    @Bean
    public KafkaReceiver<String, ClientMessage> kafkaReceiver() {
        ReceiverOptions<String, ClientMessage> receiverOptions = ReceiverOptions.<String, ClientMessage>create(getProperties("consumer.properties"))
                .subscription(Collections.singleton(topicName));
        return KafkaReceiver.create(receiverOptions);
    }


}
