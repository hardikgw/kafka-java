package biz.cits.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class App {

    @Value( "${kafka.topic}" )
    private String topic;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name(topic).partitions(1).replicas(1).build();
    }
}
