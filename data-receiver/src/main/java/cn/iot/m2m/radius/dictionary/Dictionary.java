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
public interface Dictionary {

	/**
	 * Retrieves an attribute type by name. This includes
	 * vendor-specific attribute types whose name is prefixed
	 * by the vendor name. 
	 * @param typeName name of the attribute type 
	 * @return AttributeType object or null
	 */
	 AttributeType getAttributeTypeByName(String typeName);
	
	/**
	 * Retrieves an attribute type by type code. This method
	 * does not retrieve vendor specific attribute types.
	 * @param typeCode type code, 1-255
	 * @return AttributeType object or null
	 */
	 AttributeType getAttributeTypeByCode(int typeCode);
	
	/**
	 * Retrieves an attribute type for a vendor-specific
	 * attribute.
	 * @param vendorId vendor ID
	 * @param typeCode type code, 1-255
	 * @return AttributeType object or null
	 */
	 AttributeType getAttributeTypeByCode(int vendorId, int typeCode);

	/**
	 * Retrieves the name of the vendor with the given
	 * vendor code.
	 * @param vendorId vendor number
	 * @return vendor name or null
	 */
	 String getVendorName(int vendorId);
	
	/**
	 * Retrieves the ID of the vendor with the given
	 * name.
	 * @param vendorName name of the vendor
	 * @return vendor ID or -1
	 */
	 int getVendorId(String vendorName);
	
}
