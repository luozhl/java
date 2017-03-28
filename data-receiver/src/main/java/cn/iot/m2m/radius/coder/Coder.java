package cn.iot.m2m.radius.coder;

import cn.iot.m2m.radius.packet.RadiusPacket;
import cn.iot.m2m.radius.util.RadiusUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 *
 * @createTime: 2015-4-20 上午11:12:03
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime: 
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum: 
 * 
 */

/**
 * @author Tibbers
 *
 */
public class Coder {
	
	private String sharedSecret;

	private MessageDigest md5Digest;
	
	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}


	/**
	 * Returns a MD5 digest.
	 * @return MessageDigest object
	 */
	protected MessageDigest getMd5Digest() {
		if (md5Digest == null)
			try {
				md5Digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				throw new RuntimeException("md5 digest not available", nsae);
			}
		return md5Digest;
	}
	

	/**
	 *
	 * @param packetLength
	 * @param attributes
	 * @param rp
	 * @return
	 * @auther <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
	 * 2015-4-23 上午10:37:58
	 */
	protected byte[] updateRequestAuthenticator(int packetLength, byte[] attributes, RadiusPacket rp) {
		byte[] authenticator = new byte[16];
		for (int i = 0; i < 16; i++)
			authenticator[i] = 0;
		
		MessageDigest md5 = getMd5Digest();
        md5.reset();
        md5.update((byte)rp.getPacketType());
        md5.update((byte)rp.getPacketIdentifier());
        md5.update((byte)(packetLength >> 8));
        md5.update((byte)(packetLength & 0xff));
        md5.update(authenticator, 0, authenticator.length);
        md5.update(attributes, 0, attributes.length);
        md5.update(RadiusUtil.getUtf8Bytes(sharedSecret));
        return md5.digest();
	}
}
