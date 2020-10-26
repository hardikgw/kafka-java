package biz.cits.kafka.web;

import biz.cits.kafka.service.ClientMessage;
import biz.cits.kafka.service.MsgGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.streams.kstream.KStream;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.util.UUID;

@RestController
public class KafkaController {

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @Autowired
    KafkaReceiver<String,String> kafkaReceiver;

    @Autowired
    private KStream<String, String> stream;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


    @GetMapping(value = "/produce", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> produce(@RequestParam int num) {
        Flux<String> messages = Flux.create(messageFluxSink ->
                MsgGenerator.getMessages(num).forEach(message -> {
                    ClientMessage clientMessage = ClientMessage.builder()
                            .client(message.getKey())
                            .id(UUID.randomUUID().toString())
                            .content(message.getValue())
                            .messageDateTime(Instant.now()).build();
                    String jsonString = "";
                    try {
                        jsonString = mapper.writeValueAsString(clientMessage);

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    template.send(topic, jsonString);
                    messageFluxSink.next(jsonString);
                }));
        return messages;
    }

    @GetMapping(value = "/consume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> consume() {
        Flux<ReceiverRecord<String,String>> kafkaFlux = kafkaReceiver.receive();
        return kafkaFlux.checkpoint("Messages are started being consumed").log().doOnNext(r -> r.receiverOffset().acknowledge()).map(ReceiverRecord::value).checkpoint("Messages are done consumed");
    }

}
