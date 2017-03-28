package cn.iot.m2m.radius.dictionary;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class MemoryDictionary implements WritableDictionary {

    private Map<Integer, String> vendorsByCode = new HashMap<>();        // <Integer, String>

    private Map<Integer, Map<Integer, AttributeType>> attributesByCode = new HashMap<>();    // <Integer, <Integer, AttributeType>>

    private Map<String, AttributeType> attributesByName = new HashMap<>();    // <String, AttributeType>

    /**
     * Returns the AttributeType for the vendor -1 from the
     * cache.
     *
     * @param typeCode attribute type code
     * @return AttributeType or null
     */
    @Override
    public AttributeType getAttributeTypeByCode(int typeCode) {
        return getAttributeTypeByCode(-1, typeCode);
    }

    /**
     * Returns the specified AttributeType object.
     *
     * @param vendorCode vendor ID or -1 for "no vendor"
     * @param typeCode   attribute type code
     * @return AttributeType or null
     */
    @Override
    public AttributeType getAttributeTypeByCode(int vendorCode, int typeCode) {
        Map<Integer, AttributeType> vendorAttributes = attributesByCode.get(vendorCode);
        if (vendorAttributes == null)
            return null;
        else
            return vendorAttributes.get(Integer.valueOf(typeCode));
    }

    /**
     * Retrieves the attribute type with the given name.
     *
     * @param typeName name of the attribute type
     * @return AttributeType or null
     */
    @Override
    public AttributeType getAttributeTypeByName(String typeName) {
        return attributesByName.get(typeName);
    }

    /**
     * Searches the vendor with the given name and returns its
     * code. This method is seldomly used.
     *
     * @param vendorName vendor name
     * @return vendor code or -1
     */
    @Override
    public int getVendorId(String vendorName) {
        for (Iterator<Map.Entry<Integer, String>> i = vendorsByCode.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<Integer, String> e = i.next();
            if (e.getValue().equals(vendorName))
                return e.getKey();
        }
        return -1;
    }

    /**
     * Retrieves the name of the vendor with the given code from
     * the cache.
     *
     * @param vendorId vendor number
     * @return vendor name or null
     */
    @Override
    public String getVendorName(int vendorId) {
        return vendorsByCode.get(Integer.valueOf(vendorId));
    }

    /**
     * Adds the given vendor to the cache.
     *
     * @param vendorId   vendor ID
     * @param vendorName name of the vendor
     * @throws IllegalArgumentException empty vendor name, invalid vendor ID
     */
    @Override
    public void addVendor(int vendorId, String vendorName) {
        if (vendorId < 0)
            throw new IllegalArgumentException("vendor ID must be positive");
        if (getVendorName(vendorId) != null)
            throw new IllegalArgumentException("duplicate vendor code");
        if (vendorName == null || vendorName.length() == 0)
            throw new IllegalArgumentException("vendor name empty");
        vendorsByCode.put(vendorId, vendorName);
    }

    /**
     * Adds an AttributeType object to the cache.
     *
     * @param attributeType AttributeType object
     * @throws IllegalArgumentException duplicate attribute name/type code
     */
    @Override
    public void addAttributeType(AttributeType attributeType) {
        if (attributeType == null)
            throw new IllegalArgumentException("attribute type must not be null");

        Integer vendorId = Integer.valueOf(attributeType.getVendorId());
        Integer typeCode = Integer.valueOf(attributeType.getTypeCode());
        String attributeName = attributeType.getName();

        if (attributesByName.containsKey(attributeName))
            throw new IllegalArgumentException("duplicate attribute name: " + attributeName);

        Map<Integer, AttributeType> vendorAttributes = attributesByCode.get(vendorId);
        if (vendorAttributes == null) {
            vendorAttributes = new HashMap<>();
            attributesByCode.put(vendorId, vendorAttributes);
        }
        if (vendorAttributes.containsKey(typeCode))
            throw new IllegalArgumentException("duplicate type code: " + typeCode);

        attributesByName.put(attributeName, attributeType);
        vendorAttributes.put(typeCode, attributeType);
    }


}