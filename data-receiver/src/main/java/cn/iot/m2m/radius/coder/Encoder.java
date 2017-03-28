package cn.iot.m2m.radius.coder;

import cn.iot.m2m.radius.attribute.RadiusAttribute;
import cn.iot.m2m.radius.packet.RadiusPacket;
import cn.iot.m2m.radius.util.RadiusUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;

/**
 * 
 *
 * @createTime: 2015-4-19 下午11:45:02
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime: 
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum: 
 * 
 */
public class Encoder extends Coder {

	/**
	 * Next packet identifier.
	 */
	private static int nextPacketId = 0;

	/**
	 * Random number generator.
	 */
	private static SecureRandom random = new SecureRandom();
	
	/**
	 * Passphrase Authentication Protocol
	 */
	public static final String AUTH_PAP = "pap";
	
	/**
	 * Challenged Handshake Authentication Protocol
	 */
	public static final String AUTH_CHAP = "chap";
	
	public byte[] encodePacket(RadiusPacket rp) 
			throws IOException {
		return encodePacket(rp, null);
	}
	

	/**
	 *
	 * @param rp
	 * @param authProtocol
	 * @return
	 * @throws IOException
	 * @auther <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
	 * 2015-4-23 上午10:38:49
	 */
	public byte[] encodePacket(RadiusPacket rp, String authProtocol) throws IOException {
		// check shared secret
		if (getSharedSecret() == null || getSharedSecret().length() == 0)
			throw new RuntimeException("no shared secret has been set");
		if (rp == null)
			throw new RuntimeException("no radius packet has been set");
		if (rp.getPacketType() < 1)
			throw new RuntimeException("no radius packet type has been set");
		
		if (rp.getPacketIdentifier() < 1) {
			rp.setPacketIdentifier(getNextPacketIdentifier());
		}
		
		byte[] authenticator = createRequestAuthenticator(getSharedSecret());
			
		byte[] attributes = getAttributeBytes(rp.getAttributes());
		int packetLength = RadiusPacket.RADIUS_HEADER_LENGTH + attributes.length;
		if (packetLength > RadiusPacket.MAX_PACKET_LENGTH)
			throw new RuntimeException("packet too long");
		if(RadiusPacket.ACCESS_REQUEST==rp.getPacketType()) {
			encodeRequestAttributes(rp, authProtocol, authenticator);
		} else if(RadiusPacket.ACCOUNTING_REQUEST == rp.getPacketType()) {
			authenticator = updateRequestAuthenticator(packetLength, attributes, rp);			
		}
		rp.setAuthenticator(authenticator);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeByte(rp.getPacketType());
		dos.writeByte(rp.getPacketIdentifier());
		dos.writeShort(packetLength);
		dos.write(rp.getAuthenticator());
		dos.write(attributes);
		dos.flush();
		return os.toByteArray();
	}
	
	/**
	 * Sets and encrypts the User-Password attribute.
	 * @see org.tinyradius.packet.RadiusPacket#encodeRequestAttributes(String)
	 */
	protected void encodeRequestAttributes(RadiusPacket rp, String authProtocol, byte[] authenticator) {
		RadiusAttribute userPassword = rp.getAttribute(RadiusPacket.USER_PASSWORD);
		String password = userPassword.getAttributeValue();
		if (password == null || password.length() == 0)
			return;
			// ok for proxied packets whose CHAP password is already encrypted
		if (authProtocol.equals(AUTH_PAP)) {
			byte[] pass = encodePapPassword(RadiusUtil.getUtf8Bytes(password), authenticator);
			rp.removeAttributes(RadiusPacket.USER_PASSWORD);
			rp.addAttribute(new RadiusAttribute(RadiusPacket.USER_PASSWORD, pass));
		} else if (authProtocol.equals(AUTH_CHAP)) {
			byte[] challenge = createChapChallenge();
			byte[] pass = encodeChapPassword(password, challenge);
			rp.removeAttributes(RadiusPacket.CHAP_PASSWORD);
			rp.removeAttributes(RadiusPacket.CHAP_CHALLENGE);
			rp.addAttribute(new RadiusAttribute(RadiusPacket.CHAP_PASSWORD, pass));
			rp.addAttribute(new RadiusAttribute(RadiusPacket.CHAP_CHALLENGE, challenge));
		}
	}

	/**
	 * Encodes the attributes of this Radius packet to a byte array.
	 * @return byte array with encoded attributes
	 * @throws IOException error writing data
	 */
	protected byte[] getAttributeBytes(List<RadiusAttribute> attributes) 
	throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(RadiusPacket.MAX_PACKET_LENGTH);
		for (Iterator<RadiusAttribute> i = attributes.iterator(); i.hasNext();) {
			RadiusAttribute a = i.next();
			bos.write(a.writeAttribute());
		}
		bos.flush();
		return bos.toByteArray();
	}
	
	protected byte[] createRequestAuthenticator(String sharedSecret) {
		byte[] secretBytes = RadiusUtil.getUtf8Bytes(sharedSecret);
		byte[] randomBytes = new byte[16];
		random.nextBytes(randomBytes);

		MessageDigest md5 = getMd5Digest();
		md5.reset();
		md5.update(secretBytes);
		md5.update(randomBytes);
		return md5.digest();
	}
	

	/**
	 * Retrieves the next packet identifier to use and increments the static
	 * storage.
	 * @return the next packet identifier to use
	 */
	public static synchronized int getNextPacketIdentifier(){
		nextPacketId++;
		if (nextPacketId > 255)
			nextPacketId = 0;
		return nextPacketId;
	}
	
	/**
	 * This method encodes the plaintext user password according to RFC 2865.
	 * @param userPass the password to encrypt
	 * @param sharedSecret shared secret
	 * @return the byte array containing the encrypted password
	 */
	private byte[] encodePapPassword(final byte[] userPass, byte[] authenticator) {
	    // the password must be a multiple of 16 bytes and less than or equal
	    // to 128 bytes. If it isn't a multiple of 16 bytes fill it out with zeroes
	    // to make it a multiple of 16 bytes. If it is greater than 128 bytes
	    // truncate it at 128.
	    byte[] userPassBytes ;
	    if (userPass.length > 128){
	        userPassBytes = new byte[128];
	        System.arraycopy(userPass, 0, userPassBytes, 0, 128);
	    } else {
	        userPassBytes = userPass;
	    }
	    
	    // declare the byte array to hold the final product
	    byte[] encryptedPass ;
	    if (userPassBytes.length < 128) {
	        if (userPassBytes.length % 16 == 0) {
	            // tt is already a multiple of 16 bytes
	            encryptedPass = new byte[userPassBytes.length];
	        } else {
	            // make it a multiple of 16 bytes
	            encryptedPass = new byte[((userPassBytes.length / 16) * 16) + 16];
	        }
	    } else {
	        // the encrypted password must be between 16 and 128 bytes
	        encryptedPass = new byte[128];
	    }
	
	    // copy the userPass into the encrypted pass and then fill it out with zeroes
	    System.arraycopy(userPassBytes, 0, encryptedPass, 0, userPassBytes.length);
	    for (int i = userPassBytes.length; i < encryptedPass.length; i++) {
	        encryptedPass[i] = 0;
	    }
	
	    // digest shared secret and authenticator
	    MessageDigest md5 = getMd5Digest();
		byte[] lastBlock = new byte[16];
		
		for (int i = 0; i < encryptedPass.length; i+=16) {
			md5.reset();
			md5.update(RadiusUtil.getUtf8Bytes(getSharedSecret()));
			md5.update(i == 0 ? authenticator : lastBlock);
			byte[] bn = md5.digest();
				
			System.arraycopy(encryptedPass, i, lastBlock, 0, 16);
		
			// perform the XOR as specified by RFC 2865.
			for (int j = 0; j < 16; j++)
				encryptedPass[i + j] = (byte)(bn[j] ^ encryptedPass[i + j]);
		}
	    
	    return encryptedPass;
	}
	
	/**
	 * Encodes a plain-text password using the given CHAP challenge.
	 * @param plaintext plain-text password
	 * @param chapChallenge CHAP challenge
	 * @return CHAP-encoded password
	 */
	private byte[] encodeChapPassword(String plaintext, byte[] chapChallenge) {
        // see RFC 2865 section 2.2
        byte chapIdentifier = (byte)random.nextInt(256);
        byte[] chapPassword = new byte[17];
        chapPassword[0] = chapIdentifier;

        MessageDigest md5 = getMd5Digest();
        md5.reset();
        md5.update(chapIdentifier);
        md5.update(RadiusUtil.getUtf8Bytes(plaintext));
        byte[] chapHash = md5.digest(chapChallenge);

        System.arraycopy(chapHash, 0, chapPassword, 1, 16);
        return chapPassword;
	}
	
	/**
	 * Creates a random CHAP challenge using a secure random algorithm.
	 * @return 16 byte CHAP challenge
	 */
	private byte[] createChapChallenge() {
		byte[] challenge = new byte[16];
		random.nextBytes(challenge);
		return challenge;
	}
}
