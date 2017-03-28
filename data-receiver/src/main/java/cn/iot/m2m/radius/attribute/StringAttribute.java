package cn.iot.m2m.radius.attribute;

import java.io.UnsupportedEncodingException;

/**
 * 
 *
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime: 
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum: 
 * 
 */
public class StringAttribute extends RadiusAttribute {

	/**
	 * Constructs an empty string attribute.
	 */
	public StringAttribute() {
		super();
	}
	
	/**
	 * Constructs a string attribute with the given value.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public StringAttribute(int type, String value) {
		setAttributeType(type);
		setAttributeValue(value);
	}
	
	/**
	 * Returns the string value of this attribute.
	 * @return a string
	 */
	@Override
	public String getAttributeValue() {
		try {
			return new String(getAttributeData(), "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return new String(getAttributeData());
		}
	}
	
	/**
	 * Sets the string value of this attribute.
	 * @param value string, not null
	 */
	@Override
	public void setAttributeValue(String value) {
		if (value == null)
			throw new NullPointerException("string value not set");
		try {
			setAttributeData(value.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException uee) {
			setAttributeData(value.getBytes());
		}
	}
	
}
