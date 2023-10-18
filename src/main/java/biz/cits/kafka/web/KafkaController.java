package biz.cits.kafka.web;

import biz.cits.kafka.service.ClientMessageReceiver;
import biz.cits.kafka.service.ClientMessageSender;
import biz.cits.kafka.service.MsgGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kafka.client.event.ClientMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class KafkaController {

    final ClientMessageSender sender;
    final ClientMessageReceiver receiver;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Autowired
    public KafkaController(ClientMessageSender sender, ClientMessageReceiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }


    @GetMapping(value = "/produce", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String produce(@RequestParam int num) {
        Flux<ClientMessage> messages = Flux.create(messageFluxSink ->
                MsgGenerator.getMessages(num).forEach(message -> {
                    ClientMessage clientMessage = ClientMessage.newBuilder()
                            .setId(UUID.randomUUID().toString())
                            .setContent(message.getValue())
                            .setMessageDateTime(Instant.now().toEpochMilli())
                            .build();
                    ArrayList<ClientMessage> clientMessages = new ArrayList<>();
                    clientMessages.add(clientMessage);
                    sender.sendMessages(clientMessages);
                    messageFluxSink.next(clientMessage);
                }));
        return "ok";
    }

    @GetMapping(value = "/consume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ConsumerRecord<String, ClientMessage>> consume() {
        return receiver.receiveMessages();
    }

}
