package cn.iot.m2m.radius.attribute;

import cn.iot.m2m.radius.dictionary.AttributeType;
import cn.iot.m2m.radius.util.RadiusException;


/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class IntegerAttribute extends RadiusAttribute {

    /**
     * 默认构造方法
     */
    public IntegerAttribute() {
        super();
    }

    /**
     * 带参数的构造方法.
     *
     * @param type  attribute type
     * @param value attribute value
     */
    public IntegerAttribute(int type, int value) {
        setAttributeType(type);
        setAttributeValue(value);
    }

    /**
     * @return a string
     */
    public int getAttributeValueInt() {
        byte[] data = getAttributeData();
        return ((data[0] & 0x0ff) << 24) | ((data[1] & 0x0ff) << 16) |
                ((data[2] & 0x0ff) << 8) | (data[3] & 0x0ff);
    }

    /**
     * @see org.tinyradius.attribute.RadiusAttribute#getAttributeValue()
     */
    @Override
    public String getAttributeValue() {
        int value = getAttributeValueInt();
        AttributeType at = getAttributeTypeObject();
        if (at != null) {
            String name = at.getEnumeration(value);
            if (name != null)
                return name;
        }

        return Integer.toString(value);
    }

    /**
     * Sets the value of this attribute.
     *
     * @param value integer value
     */
    public void setAttributeValue(int value) {
        byte[] data = new byte[4];
        data[0] = (byte) (value >> 24 & 0x0ff);
        data[1] = (byte) (value >> 16 & 0x0ff);
        data[2] = (byte) (value >> 8 & 0x0ff);
        data[3] = (byte) (value & 0x0ff);
        setAttributeData(data);
    }

    /**
     * @throws NumberFormatException if value is not a number and constant cannot be resolved
     */
    @Override
    public void setAttributeValue(String value) {
        AttributeType at = getAttributeTypeObject();
        if (at != null) {
            Integer val = at.getEnumeration(value);
            if (val != null) {
                setAttributeValue(val.intValue());
                return;
            }
        }
        setAttributeValue(Integer.parseInt(value));
    }

    @Override
    public void readAttribute(byte[] data, int offset, int length) throws RadiusException {
        if (length != 6)
            throw new RadiusException("integer attribute: expected 4 bytes data");
        super.readAttribute(data, offset, length);
    }

}
