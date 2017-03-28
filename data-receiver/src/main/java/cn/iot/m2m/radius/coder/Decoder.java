package cn.iot.m2m.radius.coder;

import cn.iot.m2m.radius.attribute.RadiusAttribute;
import cn.iot.m2m.radius.dictionary.DefaultDictionary;
import cn.iot.m2m.radius.dictionary.Dictionary;
import cn.iot.m2m.radius.packet.RadiusPacket;
import cn.iot.m2m.radius.util.RadiusException;
import cn.iot.m2m.radius.util.RadiusUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @createTime: 2015-4-19 下午11:45:02
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class Decoder extends Coder {

    private Dictionary dictionary;

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * @param in
     * @return
     * @throws IOException
     * @throws RadiusException
     * @auther <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
     * 2015-4-23 上午10:38:21
     */
    public RadiusPacket decodePacket(InputStream in) throws IOException, RadiusException {
        // read and check header
        int type = in.read() & 0x0ff;
        int identifier = in.read() & 0x0ff;
        int length = (in.read() & 0x0ff) << 8 | (in.read() & 0x0ff);

        if (length < RadiusPacket.RADIUS_HEADER_LENGTH)
            throw new RadiusException("bad packet: packet too short (" + length + " bytes)");
        if (length > RadiusPacket.MAX_PACKET_LENGTH)
            throw new RadiusException("bad packet: packet too long (" + length + " bytes)");

        // read rest of packet
        byte[] authenticator = new byte[16];
        byte[] attributeData = new byte[length - RadiusPacket.RADIUS_HEADER_LENGTH];
        in.read(authenticator);
        in.read(attributeData);

        // check and count attributes
        int pos = 0;
        while (pos < attributeData.length) {
            if (pos + 1 >= attributeData.length)
                throw new RadiusException("bad packet: attribute length mismatch");
            int attributeLength = attributeData[pos + 1] & 0x0ff;
            if (attributeLength < 2)
                throw new RadiusException("bad packet: invalid attribute length");
            pos += attributeLength;
        }
        if (pos != attributeData.length)
            throw new RadiusException("bad packet: attribute length mismatch");

        // create RadiusPacket object; set properties
        RadiusPacket rp = new RadiusPacket();
        rp.setPacketType(type);
        rp.setPacketIdentifier(identifier);
        rp.setAuthenticator(authenticator);
        if (dictionary == null) {
            rp.setDictionary(DefaultDictionary.getDefaultDictionary());
        } else {
            rp.setDictionary(getDictionary());
        }

        // load attributes
        pos = 0;
        while (pos < attributeData.length) {
            int attributeType = attributeData[pos] & 0x0ff;
            int attributeLength = attributeData[pos + 1] & 0x0ff;
            RadiusAttribute a = RadiusAttribute.createRadiusAttribute(dictionary, -1, attributeType);
            a.readAttribute(attributeData, pos, attributeLength);
            rp.addAttribute(a);
            pos += attributeLength;
        }
        if (RadiusPacket.ACCESS_REQUEST == type) {
            decodeRequestAttributes(rp);
        }
        return rp;
    }

    /**
     * Decrypts the User-Password attribute.
     *
     */
    protected void decodeRequestAttributes(RadiusPacket rp)
            throws RadiusException {
        // detect auth protocol
        RadiusAttribute userPassword = rp.getAttribute(RadiusPacket.USER_PASSWORD);
        RadiusAttribute chapPassword = rp.getAttribute(RadiusPacket.CHAP_PASSWORD);
        RadiusAttribute chapChallenge = rp.getAttribute(RadiusPacket.CHAP_CHALLENGE);

        if (userPassword != null) {
            String password = decodePapPassword(userPassword.getAttributeData(), rp.getAuthenticator());
            // copy truncated data
            userPassword.setAttributeData(RadiusUtil.getUtf8Bytes(password));
        } else {
            throw new RadiusException("Access-Request: User-Password or CHAP-Password/CHAP-Challenge missing");
        }
    }

    /**
     * Decodes the passed encrypted password and returns the clear-text form.
     *
     * @param encryptedPass encrypted password
     * @param authenticator
     * @return decrypted password
     */
    private String decodePapPassword(byte[] encryptedPass, byte[] authenticator)
            throws RadiusException {
        if (encryptedPass == null || encryptedPass.length < 16) {
            throw new RadiusException("malformed User-Password attribute");
        }

        MessageDigest md5 = getMd5Digest();
        byte[] lastBlock = new byte[16];

        for (int i = 0; i < encryptedPass.length; i += 16) {
            md5.reset();
            md5.update(RadiusUtil.getUtf8Bytes(getSharedSecret()));
            md5.update(i == 0 ? authenticator : lastBlock);
            byte[] bn = md5.digest();

            System.arraycopy(encryptedPass, i, lastBlock, 0, 16);

            // perform the XOR as specified by RFC 2865.
            for (int j = 0; j < 16; j++)
                encryptedPass[i + j] = (byte) (bn[j] ^ encryptedPass[i + j]);
        }

        // remove trailing zeros
        int len = encryptedPass.length;
        while (len > 0 && encryptedPass[len - 1] == 0)
            len--;
        byte[] passtrunc = new byte[len];
        System.arraycopy(encryptedPass, 0, passtrunc, 0, len);

        // convert to string
        return RadiusUtil.getStringFromUtf8(passtrunc);
    }

    /**
     * Checks the received request authenticator as specified by RFC 2866.
     */
    protected void checkRequestAuthenticator(int packetLength, byte[] attributes, RadiusPacket rp) throws RadiusException {
        byte[] expectedAuthenticator = updateRequestAuthenticator(packetLength, attributes, rp);
        byte[] receivedAuth = rp.getAuthenticator();
        for (int i = 0; i < 16; i++)
            if (expectedAuthenticator[i] != receivedAuth[i])
                throw new RadiusException("request authenticator invalid.");
    }
}
