package biz.cits.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;

import java.util.Collections;

@SpringBootApplication
public class App {

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    KafkaProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topic).partitions(30).replicas(1).build();
    }

    @Bean
    public KStream<String, String> kStream() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(topic, Consumed.with(Serdes.String(), new JsonSerde<>(String.class)));
        stream.foreach((m,n) -> System.out.println(">>>>>>>>>>>>>> Stream >>>>>>>>>>>>>>>>>>" + n));
        return stream;
    }

    @Bean
    public KafkaReceiver kafkaReceiver(){
        return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE, ReceiverOptions.create(properties.buildConsumerProperties()).subscription(Collections.singleton(topic)));
    }

    @KafkaListener(id = "local", topics = "${kafka.topic}")
    public void kafkaListener(String data) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>" + data);
    }
}
