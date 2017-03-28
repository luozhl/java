package com.cmiot.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyProducer {
    private static final String TOPIC = "GGSN_STATUS_L";
    private static final String CONTENT = "This is a single message";
    private static final String BROKER_LIST = "node134:6667,node135:6667,node136:6667";
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER_CLASS);
        props.put("metadata.broker.list", BROKER_LIST);

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        //Send one message.
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(TOPIC, CONTENT);
        producer.send(message);

        //Send multiple messages.
        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>();
        for (int i = 1; i < 6; i++) {
            messages.add(new KeyedMessage<String, String>(TOPIC, "Multiple message at a time " + i));
        }
        producer.send(messages);
    }
}
