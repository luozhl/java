package cn.iot.m2m.radius.attribute;

import cn.iot.m2m.radius.util.RadiusException;

import java.util.StringTokenizer;

/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class IpAttribute extends RadiusAttribute {

    /**
     * Constructs an empty IP attribute.
     */
    public IpAttribute() {
        super();
    }

    /**
     * Constructs an IP attribute.
     *
     * @param type  attribute type code
     * @param value value, format: xx.xx.xx.xx
     */
    public IpAttribute(int type, String value) {
        setAttributeType(type);
        setAttributeValue(value);
    }

    /**
     * Constructs an IP attribute.
     *
     * @param type  attribute type code
     * @param ipNum value as a 32 bit unsigned int
     */
    public IpAttribute(int type, long ipNum) {
        setAttributeType(type);
        setIpAsLong(ipNum);
    }

    /**
     * Returns the attribute value (IP number) as a string of the
     * format "xx.xx.xx.xx".
     */
    @Override
    public String getAttributeValue() {
        StringBuilder ip = new StringBuilder();
        byte[] data = getAttributeData();
        if (data == null || data.length != 4) {
            throw new RuntimeException("ip attribute: expected 4 bytes attribute data");
        }
        ip.append(data[0] & 0x0ff);
        ip.append(".");
        ip.append(data[1] & 0x0ff);
        ip.append(".");
        ip.append(data[2] & 0x0ff);
        ip.append(".");
        ip.append(data[3] & 0x0ff);

        return ip.toString();
    }

    /**
     * Sets the attribute value (IP number). String format:
     * "xx.xx.xx.xx".
     *
     * @throws IllegalArgumentException
     * @throws NumberFormatException
     */
    @Override
    public void setAttributeValue(String value) {
        if (value == null || value.length() < 7 || value.length() > 15)
            throw new IllegalArgumentException("bad IP number");

        StringTokenizer tok = new StringTokenizer(value, ".");
        if (tok.countTokens() != 4)
            throw new IllegalArgumentException("bad IP number: 4 numbers required");

        byte[] data = new byte[4];
        for (int i = 0; i < 4; i++) {
            int num = Integer.parseInt(tok.nextToken());
            if (num < 0 || num > 255)
                throw new IllegalArgumentException("bad IP number: num out of bounds");
            data[i] = (byte) num;
        }

        setAttributeData(data);
    }

    /**
     * Returns the IP number as a 32 bit unsigned number. The number is
     * returned in a long because Java does not support unsigned ints.
     *
     * @return IP number
     */
    public long getIpAsLong() {
        byte[] data = getAttributeData();
        if (data == null || data.length != 4) {
            throw new RuntimeException("expected 4 bytes attribute data");
        }
        return ((long) (data[0] & 0x0ff)) << 24 | (data[1] & 0x0ff) << 16 |
                (data[2] & 0x0ff) << 8 | (data[3] & 0x0ff);
    }

    /**
     * Sets the IP number represented by this IpAttribute
     * as a 32 bit unsigned number.
     *
     * @param ip
     */
    public void setIpAsLong(long ip) {
        byte[] data = new byte[4];
        data[0] = (byte) ((ip >> 24) & 0x0ff);
        data[1] = (byte) ((ip >> 16) & 0x0ff);
        data[2] = (byte) ((ip >> 8) & 0x0ff);
        data[3] = (byte) (ip & 0x0ff);
        setAttributeData(data);
    }

    /**
     * Check attribute length.
     */
    @Override
    public void readAttribute(byte[] data, int offset, int length)
            throws RadiusException {
        if (length != 6) {
            throw new RadiusException("IP attribute: expected 4 bytes data");
        }
        super.readAttribute(data, offset, length);
    }

}
