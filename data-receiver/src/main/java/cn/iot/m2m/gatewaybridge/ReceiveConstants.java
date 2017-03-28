package cn.iot.m2m.gatewaybridge;

/**
 * @author: leihang@live.cn
 * @date: 2017-03-07 11:09
 * @description:
 */

public class ReceiveConstants {

    /**
     * Radius报文中对应MSISDN的类型ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_MSISDN = 31;
    /**
     * Radius报文中对应IP的类型ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_IP = 8;
    /**
     * Radius报文中对应STATUS的类型ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_STATUS = 40;
    /**
     * Radius报文中对应APNID的类型ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_APNID = 30;
    /**
     * Radius报文中对应MSISDN的类型ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_TIMESTAMP = 55;
    /**
     * Radius报文中对应3GPP的类型vendor ID，该节点的主类型ID为26
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_3GPP = 10415;
    /**
     * Radius报文中对应ACCESSTYPE的类型vendor子 ID
     */
    public static final int RADIUS_ATTR_TYPE_GPRS_ACCESSTYPE = 21;

    private ReceiveConstants() {
    }
}
