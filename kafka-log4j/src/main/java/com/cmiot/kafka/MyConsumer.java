package com.cmiot.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyConsumer {
    private static final String ZOOKEEPER = "node131:2181,node132:2181,node133:2181";
    private static final String GROUP_NAME = "GGSN_GROUP";
    private static final String TOPIC_NAME = "GGSN_STATUS_LH";
    private static final int CONSUMER_NUM = 1;
    private static final int PARTITION_NUM = 1;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("zookeeper.connect", ZOOKEEPER);
        props.put("zookeeper.connectiontimeout.ms", "1000000");
        props.put("group.id", GROUP_NAME);

        ConsumerConfig consumerConfig = new ConsumerConfig(props);
        ConsumerConnector consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        Map<String, Integer> consumeInfo = new HashMap<String, Integer>();
        consumeInfo.put(TOPIC_NAME, PARTITION_NUM);
        Map<String, List<KafkaStream<byte[], byte[]>>> topicMessageStreams = consumerConnector
                .createMessageStreams(consumeInfo);
        List<KafkaStream<byte[], byte[]>> streams = topicMessageStreams.get(TOPIC_NAME);

        ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_NUM);

        for (final KafkaStream<byte[], byte[]> stream : streams) {
            executor.submit(new Runnable() {
                public void run() {
                    for (MessageAndMetadata<byte[], byte[]> msgAndMetadata : stream) {
                        String message = new String(msgAndMetadata.message());
                        System.out.println(message);
                              System.out.println(message.substring(message.indexOf("- {")+2,message.length()));

                    }
                }
            });
        }
    }

}
