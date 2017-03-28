package cn.iot.m2m.gatewaybridge.deal;

import cn.iot.m2m.gatewaybridge.CmSubsGprsStatus;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * 用于序列化对象后传输到kafka
 * @author luozhl
 * @date 2017/3/28
 */
public class ObjectSerializer  implements Serializer<CmSubsGprsStatus> {

    private static Logger LOGGER = Logger.getLogger(ObjectSerializer.class);

    private String encoding = "UTF8";


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey?"key.serializer.encoding":"value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if(encodingValue == null) {
            encodingValue = configs.get("serializer.encoding");
        }
        if(encodingValue != null && encodingValue instanceof String) {
            this.encoding = (String)encodingValue;
        }

    }

    @Override
    public byte[] serialize(String topic, CmSubsGprsStatus cmSubsGprsStatus) {
        //序列化对象后写入kafka
        if(cmSubsGprsStatus == null){
            return new byte[0];
        }
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            try ( ObjectOutputStream oos = new ObjectOutputStream(bos)){
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(bo);
                oos.writeObject(cmSubsGprsStatus);
                return bo.toByteArray();
            }catch (Exception e){
                LOGGER.error("ObjectSerializer 序列化"+cmSubsGprsStatus+"失败",e);
            }
        }catch (Exception e){
            LOGGER.error("ObjectSerializer 序列化"+cmSubsGprsStatus+"失败",e);
        }
        return new byte[0];
    }

    @Override
    public void close() {

    }
}
