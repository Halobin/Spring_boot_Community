package com.halobin.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMes("test","1");
        kafkaProducer.sendMes("test","2");
        Thread.sleep(1000*10);
    }
}

@Component
class KafkaProducer{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMes(String topic, String content){
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = "test")
    public void handleMes(ConsumerRecord record){
        System.out.println(record.value());
    }
}
