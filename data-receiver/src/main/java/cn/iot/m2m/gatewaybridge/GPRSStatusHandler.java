package cn.iot.m2m.gatewaybridge;

import cn.iot.m2m.gatewaybridge.counter.Counter;
import cn.iot.m2m.gatewaybridge.deal.LogSend;
import cn.iot.m2m.radius.attribute.VendorSpecificAttribute;
import cn.iot.m2m.radius.coder.Decoder;
import cn.iot.m2m.radius.packet.RadiusPacket;
import cn.iot.m2m.radius.util.RadiusException;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;


/**
 * GGSN数据接收
 */
public class GPRSStatusHandler extends IoHandlerAdapter {

    @Autowired
    private Decoder radiusDecoder;
    @Autowired
    private LogSend logSend;

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer buffer = (IoBuffer) message;
        byte[] dst = new byte[buffer.remaining()];
        buffer.get(dst);
        InputStream in = new ByteArrayInputStream(dst);
        CmSubsGprsStatus obj = deal(in);

        //计数
        Counter.countReceiveNum1m();
        Counter.countReceiveNum5m();
        Counter.countReceiveNum60m();
        Counter.countReceiveNumDay();
        logSend.sendToKafka(obj);
    }

    public CmSubsGprsStatus deal(InputStream in) throws IOException, RadiusException {
        CmSubsGprsStatus cmSubsGprsStatus = new CmSubsGprsStatus();
        RadiusPacket rp = radiusDecoder.decodePacket(in);
        String msisdn = rp.getAttribute(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_MSISDN)
                .getAttributeValue();
        String ip = rp.getAttribute(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_IP)
                .getAttributeValue();
        String status = rp.getAttribute(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_STATUS)
                .getAttributeValue();
        String apnid = rp.getAttribute(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_APNID)
                .getAttributeValue();

        String accessType = "";

        Date statusDate = new Date();
        List<VendorSpecificAttribute> gprs3gpp = rp
                .getVendorAttributes(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_3GPP);

        for (VendorSpecificAttribute vendorSpecificAttribute : gprs3gpp) {
            if (vendorSpecificAttribute
                    .getSubAttribute(ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_ACCESSTYPE) != null) {
                accessType = vendorSpecificAttribute.getSubAttribute(
                        ReceiveConstants.RADIUS_ATTR_TYPE_GPRS_ACCESSTYPE)
                        .getAttributeValue();
                // 由于将accessType类型设置为string，故而在转换的时候是按asill码转换的，所以需要转回字节数组才能取到整型数据
                // 造成该问题的原因是GGSN在发这个字段的时候应该是int类型,而它将它处理成了string类型.
                accessType = Byte.toString(accessType.getBytes("UTF-8")[0]);
            }
        }
        if (StringUtils.isNotEmpty(msisdn)) {
            msisdn = ReceiveUtil.formatMsisdn(msisdn);
        }
        if (StringUtils.isNotEmpty(status) && "1".equals(status)) {
            status = "01";
        } else {
            status = "00";
        }
        cmSubsGprsStatus.setAccessType(accessType);
        cmSubsGprsStatus.setApnid(apnid.toUpperCase());
        cmSubsGprsStatus.setIp(ip);
        cmSubsGprsStatus.setMsisdn(msisdn);
        cmSubsGprsStatus.setStatus(status);
        cmSubsGprsStatus.setStatusDate(statusDate);
        cmSubsGprsStatus.setStatusTimestamp(statusDate.getTime());
        return cmSubsGprsStatus;
    }

}
