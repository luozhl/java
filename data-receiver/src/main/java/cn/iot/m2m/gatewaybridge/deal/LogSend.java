package cn.iot.m2m.gatewaybridge.deal;

import cn.iot.m2m.gatewaybridge.CmSubsGprsStatus;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: leihang@live.cn
 * @date: 2016-12-08 16:26
 * @description:
 */
@Component
public class LogSend {

    private static final Logger LOGGER = Logger.getLogger(LogSend.class);

    private static Gson gson = new Gson();

    @Resource
    @Qualifier("kafkaTopic")
    private  MessageChannel kafkaChannel;
    private final static String TOPIC = "test";

    private LogSend() {
    }
    public  void sendToKafka(CmSubsGprsStatus obj) {
        boolean send = kafkaChannel.send(MessageBuilder.withPayload(obj)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build());
        System.out.println("send "+send+"  "+gson.toJson(obj));
        LOGGER.info(gson.toJson(obj));
    }
}
