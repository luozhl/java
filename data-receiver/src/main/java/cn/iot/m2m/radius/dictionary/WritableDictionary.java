package cn.iot.m2m.radius.dictionary;

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
public interface WritableDictionary extends Dictionary {

	/**
	 * Adds the given vendor to the dictionary.
	 * @param vendorId vendor ID
	 * @param vendorName name of the vendor
	 */
	 void addVendor(int vendorId, String vendorName);

	/**
	 * Adds an AttributeType object to the dictionary.
	 * @param attributeType AttributeType object
	 */
	 void addAttributeType(AttributeType attributeType);

}
