package cn.iot.m2m.radius.packet;

import cn.iot.m2m.radius.attribute.RadiusAttribute;
import cn.iot.m2m.radius.attribute.VendorSpecificAttribute;
import cn.iot.m2m.radius.dictionary.AttributeType;
import cn.iot.m2m.radius.dictionary.DefaultDictionary;
import cn.iot.m2m.radius.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RadiusPacket {
	/**
	 * Type of this Radius packet.
	 */
	private int packetType = 0;

	/**
	 * Identifier of this packet.
	 */
	private int packetIdentifier = 0;

	/**
	 * Authenticator for this Radius packet.
	 */
	private byte[] authenticator = null;

	/**
	 * Attributes for this packet.
	 */
	private List<RadiusAttribute> attributes = new ArrayList<>();

	/**
	 * Dictionary to look up attribute names.
	 */
	private Dictionary dictionary;
    /**
     * Packet type codes.
     */
	public static final int ACCESS_REQUEST      = 1;
	public static final int ACCESS_ACCEPT       = 2;
	public static final int ACCESS_REJECT       = 3;
	public static final int ACCOUNTING_REQUEST  = 4;
	public static final int ACCOUNTING_RESPONSE = 5;
	public static final int ACCOUNTING_STATUS   = 6;
	public static final int PASSWORD_REQUEST    = 7;
	public static final int PASSWORD_ACCEPT     = 8;
	public static final int PASSWORD_REJECT     = 9;
	public static final int ACCOUNTING_MESSAGE  = 10;
	public static final int ACCESS_CHALLENGE    = 11;
	public static final int STATUS_SERVER       = 12;
	public static final int STATUS_CLIENT       = 13;
	public static final int DISCONNECT_REQUEST  = 40;	// RFC 2882
	public static final int DISCONNECT_ACK      = 41;
	public static final int DISCONNECT_NAK      = 42;
	public static final int COA_REQUEST         = 43;
	public static final int COA_ACK             = 44;
	public static final int COA_NAK             = 45;
	public static final int STATUS_REQUEST      = 46;
	public static final int STATUS_ACCEPT       = 47;
	public static final int STATUS_REJECT       = 48;
	public static final int RESERVED            = 255;
	
	/**
	 * Maximum packet length.
	 */
	public static final int MAX_PACKET_LENGTH = 4096;
	
	/**
	 * Packet header length.
	 */
	public static final int RADIUS_HEADER_LENGTH  = 20;
    
	/**
	 * Radius attribute type for User-Password attribute.
	 */
    public static final int USER_PASSWORD = 2;

	/**
	 * Radius attribute type for CHAP-Password attribute.
	 */
    public static final int CHAP_PASSWORD = 3;
	
	/**
	 * Radius attribute type for CHAP-Challenge attribute.
	 */
	public static final int CHAP_CHALLENGE = 60;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Returns the packet identifier for this Radius packet.
	 * @return packet identifier
	 */
	public int getPacketIdentifier() {
		return packetIdentifier;
	}
	
	/**
	 * Sets the packet identifier for this Radius packet.
	 * @param identifier packet identifier, 0-255
	 */
	public void setPacketIdentifier(int identifier) {
		if (identifier < 0 || identifier > 255)
			throw new IllegalArgumentException("packet identifier out of bounds");
		this.packetIdentifier = identifier;
	}
	
	/**
	 * Returns the type of this Radius packet.
	 * @return packet type
	 */
	public int getPacketType() {
		return packetType;
	}
	/**
	 * Sets the type of this Radius packet.
	 * @param type packet type, 0-255
	 */
	public void setPacketType(int type) {
		if (type < 1 || type > 255)
			throw new IllegalArgumentException("packet type out of bounds");
		this.packetType = type;
	}
	
	/**
	 * Sets the list of attributes for this Radius packet.
	 * @param attributes list of RadiusAttribute objects
	 */
	public void setAttributes(List<RadiusAttribute> attributes) {
		if (attributes == null)
			throw new NullPointerException("attributes list is null");
		this.attributes = attributes;
	}
	
	/**
	 * Adds a Radius attribute to this packet. Can also be used
	 * to add Vendor-Specific sub-attributes. If a attribute with
	 * a vendor code != -1 is passed in, a VendorSpecificAttribute
	 * is created for the sub-attribute.
	 * @param attribute RadiusAttribute object
	 */
	public void addAttribute(RadiusAttribute attribute) {
		if (attribute == null)
			throw new NullPointerException("attribute is null");
		attribute.setDictionary(getDictionary());
		if (attribute.getVendorId() == -1)
			this.attributes.add(attribute);
		else {
			VendorSpecificAttribute vsa = new VendorSpecificAttribute(attribute.getVendorId());
			vsa.addSubAttribute(attribute);
			this.attributes.add(vsa);
		}
	}
	
	/**
	 * Adds a Radius attribute to this packet.
	 * Uses AttributeTypes to lookup the type code and converts
	 * the value.
	 * Can also be used to add sub-attributes.
	 * @param typeName name of the attribute, for example "NAS-Ip-Address"
	 * @param value value of the attribute, for example "127.0.0.1"
	 * @throws IllegalArgumentException if type name is unknown
	 */
	public void addAttribute(String typeName, String value) {
		if (typeName == null || typeName.length() == 0)
			throw new IllegalArgumentException("type name is empty");
		if (value == null || value.length() == 0)
			throw new IllegalArgumentException("value is empty");
		
		AttributeType type = dictionary.getAttributeTypeByName(typeName);
		if (type == null)
			throw new IllegalArgumentException("unknown attribute type '" + typeName + "'");

		RadiusAttribute attribute = RadiusAttribute.createRadiusAttribute(getDictionary(), type.getVendorId(), type.getTypeCode());
		attribute.setAttributeValue(value);
		addAttribute(attribute);
	}
	
	/**
	 * Removes the specified attribute from this packet.
	 * @param attribute RadiusAttribute to remove
	 */
	public void removeAttribute(RadiusAttribute attribute) {
		if (attribute.getVendorId() == -1) {
			if (!this.attributes.remove(attribute))
				throw new IllegalArgumentException("no such attribute");
		} else {
			// remove Vendor-Specific sub-attribute
			List<VendorSpecificAttribute> vsas = getVendorAttributes(attribute.getVendorId());
			for (Iterator<VendorSpecificAttribute> i = vsas.iterator(); i.hasNext();) {
				VendorSpecificAttribute vsa = i.next();
				List<RadiusAttribute> sas = vsa.getSubAttributes(); 
				if (sas.contains(attribute)) {
					vsa.removeSubAttribute(attribute);
					if (sas.size() == 1)
						// removed the last sub-attribute
						// --> remove the whole Vendor-Specific attribute
						removeAttribute(vsa);
				}
			}		
		}
	}
	
	/**
	 * Removes all attributes from this packet which have got
	 * the specified type.
	 * @param type attribute type to remove
	 */
	public void removeAttributes(int type) {
		if (type < 1 || type > 255)
			throw new IllegalArgumentException("attribute type out of bounds");
		
		Iterator<RadiusAttribute> i = attributes.iterator();
		while (i.hasNext()) {
			RadiusAttribute attribute = i.next();
			if (attribute.getAttributeType() == type)
				i.remove();
		}
	}
	
	/**
	 * Removes the last occurence of the attribute of the given
	 * type from the packet.
	 * @param type attribute type code
	 */
	public void removeLastAttribute(int type) {
		List<RadiusAttribute> attrs = getAttributes(type);
		if (attrs == null || attrs.isEmpty())
			return;
		
		RadiusAttribute lastAttribute =
			attrs.get(attrs.size() - 1);
		removeAttribute(lastAttribute);
	}
	
	/**
	 * Removes all sub-attributes of the given vendor and
	 * type.
	 * @param vendorId vendor ID
	 * @param typeCode attribute type code
	 */
	public void removeAttributes(int vendorId, int typeCode) {
		if (vendorId == -1) {
			removeAttributes(typeCode);
			return;
		}
		
		List<VendorSpecificAttribute> vsas = getVendorAttributes(vendorId);
		for (Iterator<VendorSpecificAttribute> i = vsas.iterator(); i.hasNext();) {
			VendorSpecificAttribute vsa = i.next();
			
			List<RadiusAttribute> sas = vsa.getSubAttributes();
			for (Iterator<RadiusAttribute> j = sas.iterator(); j.hasNext();) {
				RadiusAttribute attr = j.next();
				if (attr.getAttributeType() == typeCode &&
					attr.getVendorId() == vendorId)
					j.remove();
			}
			if (sas.isEmpty())
				// removed the last sub-attribute
				// --> remove the whole Vendor-Specific attribute
				removeAttribute(vsa);
		}
	}
	
	/**
	 * Returns all attributes of this packet of the given type.
	 * Returns an empty list if there are no such attributes.
	 * @param attributeType type of attributes to get 
	 * @return list of RadiusAttribute objects, does not return null
	 */
	public List<RadiusAttribute> getAttributes(int attributeType) {
		if (attributeType < 1 || attributeType > 255)
			throw new IllegalArgumentException("attribute type out of bounds");

		LinkedList<RadiusAttribute> result = new LinkedList<>();
		for (Iterator<RadiusAttribute> i = attributes.iterator(); i.hasNext();) {
			RadiusAttribute a = i.next();
			if (attributeType == a.getAttributeType())
				result.add(a);
		}
		return result;
	}
	

	
	public void printLogAttrs(){
		for (Iterator<RadiusAttribute> i = attributes.iterator(); i.hasNext();) {
			RadiusAttribute a = i.next();
			int type = a.getAttributeType();
			String attrValStr = a.getAttributeValue();
			log.error(String.format("-----------------ggsntest-----------------------[%s=%s]-------------------------------------------", type,attrValStr));
		}
	}
	
	/**
	 * Returns all attributes of this packet that have got the
	 * given type and belong to the given vendor ID.
	 * Returns an empty list if there are no such attributes.
	 * @param vendorId vendor ID
	 * @param attributeType attribute type code
	 * @return list of RadiusAttribute objects, never null
	 */
	public List<RadiusAttribute> getAttributes(int vendorId, int attributeType) {
		if (vendorId == -1)
			return getAttributes(attributeType);
		
		LinkedList<RadiusAttribute> result = new LinkedList<>();
		List<VendorSpecificAttribute> vsas = getVendorAttributes(vendorId);
		for (Iterator<VendorSpecificAttribute> i = vsas.iterator(); i.hasNext();) {
			VendorSpecificAttribute vsa = i.next();
			List<RadiusAttribute> sas = vsa.getSubAttributes();
			for (Iterator<RadiusAttribute> j = sas.iterator(); j.hasNext();) {
				RadiusAttribute attr = j.next();
				if (attr.getAttributeType() == attributeType &&
					attr.getVendorId() == vendorId)
					result.add(attr);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a list of all attributes belonging to this Radius
	 * packet.
	 * @return List of RadiusAttribute objects
	 */
	public List<RadiusAttribute> getAttributes() {
		return attributes;
	}
	
	/**
	 * Returns a Radius attribute of the given type which may only occur once
	 * in the Radius packet.
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int type) {
		List<RadiusAttribute> attrs = getAttributes(type);
		if (attrs.size() > 1)
			throw new RuntimeException("multiple attributes of requested type " + type);
		else if (attrs.isEmpty())
			return null;
		else
			return attrs.get(0);
	}

	/**
	 * Returns a Radius attribute of the given type and vendor ID
	 * which may only occur once in the Radius packet.
	 * @param vendorId vendor ID
	 * @param type attribute type
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if there are multiple occurences of the
	 * requested attribute type
	 */
	public RadiusAttribute getAttribute(int vendorId, int type) {
		if (vendorId == -1)
			return getAttribute(type);
		
		List<RadiusAttribute> attrs = getAttributes(vendorId, type);
		if (attrs.size() > 1)
			throw new RuntimeException("multiple attributes of requested type " + type);
		else if (attrs.isEmpty())
			return null;
		else
			return attrs.get(0);
	}

	/**
	 * Returns a single Radius attribute of the given type name.
	 * Also returns sub-attributes.
	 * @param type attribute type name
	 * @return RadiusAttribute object or null if there is no such attribute
	 * @throws RuntimeException if the attribute occurs multiple times
	 */
	public RadiusAttribute getAttribute(String type) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("type name is empty");
		
		AttributeType t = dictionary.getAttributeTypeByName(type);
		if (t == null)
			throw new IllegalArgumentException("unknown attribute type name '" + type + "'");
		
		return getAttribute(t.getVendorId(), t.getTypeCode());
	}

	/**
	 * Returns the value of the Radius attribute of the given type or
	 * null if there is no such attribute.
	 * Also returns sub-attributes.
	 * @param type attribute type name
	 * @return value of the attribute as a string or null if there
	 * is no such attribute
	 * @throws IllegalArgumentException if the type name is unknown
	 * @throws RuntimeException attribute occurs multiple times
	 */
	public String getAttributeValue(String type) {
		RadiusAttribute attr = getAttribute(type);
		if (attr == null)
			return null;
		else
			return attr.getAttributeValue();
	}
	
	/**
	 * Returns the Vendor-Specific attribute(s) for the given vendor ID.
	 * @param vendorId vendor ID of the attribute(s)
	 * @return List with VendorSpecificAttribute objects, never null
	 */
	public List<VendorSpecificAttribute> getVendorAttributes(int vendorId) {
		LinkedList<VendorSpecificAttribute> result = new LinkedList<>();
		for (Iterator<RadiusAttribute> i = attributes.iterator(); i.hasNext();) {
			RadiusAttribute a = i.next();
			if (a instanceof VendorSpecificAttribute) {
				VendorSpecificAttribute vsa = (VendorSpecificAttribute)a;
				if (vsa.getChildVendorId() == vendorId)
					result.add(vsa);
			}
		}
		return result;
	}
	
	/**
	 * Returns the authenticator for this Radius packet.
	 * For a Radius packet read from a stream, this will return the
	 * authenticator sent by the server. For a new Radius packet to be sent,
	 * this will return the authenticator created by the method
	 * createAuthenticator() and will return null if no authenticator
	 * has been created yet.
	 * @return authenticator, 16 bytes
	 */
	public byte[] getAuthenticator() {		
		return authenticator;
	}
	
	/**
	 * Sets the authenticator to be used for this Radius packet.
	 * This method should seldomly be used.
	 * Authenticators are created and managed by this class internally.
	 * @param authenticator authenticator
	 */
	public void setAuthenticator(byte[] authenticator) {
		this.authenticator = authenticator;
	}
	
	/**
	 * Returns the Vendor-Specific attribute for the given vendor ID.
	 * If there is more than one Vendor-Specific
	 * attribute with the given vendor ID, the first attribute found is
	 * returned. If there is no such attribute, null is returned.
	 * @param vendorId vendor ID of the attribute
	 * @return the attribute or null if there is no such attribute
	 * @deprecated use getVendorAttributes(int)
	 * @see #getVendorAttributes(int)
	 */
	public VendorSpecificAttribute getVendorAttribute(int vendorId) {
		for (Iterator<RadiusAttribute> i = getAttributes(VendorSpecificAttribute.VENDOR_SPECIFIC).iterator(); i.hasNext();) {
			VendorSpecificAttribute vsa = (VendorSpecificAttribute)i.next();
			if (vsa.getChildVendorId() == vendorId)
				return vsa;
		}
		return null;
	}
	
	public Dictionary getDictionary() {
		if(dictionary == null) {
			dictionary = DefaultDictionary.getDefaultDictionary();
		}
		return dictionary;
	}
	
	/**
	 * Sets a custom dictionary to use. If no dictionary is set,
	 * the default dictionary is used.
	 * Also copies the dictionary to the attributes.
	 * @param dictionary Dictionary class to use
	 * @see DefaultDictionary
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
		for (Iterator<RadiusAttribute> i = attributes.iterator(); i.hasNext();) {
			RadiusAttribute attr = i.next();
			attr.setDictionary(dictionary);
		}
	}
	

	

}
