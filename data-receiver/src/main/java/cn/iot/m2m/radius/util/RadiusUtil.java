package cn.iot.m2m.radius.util;

import java.io.UnsupportedEncodingException;

/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class RadiusUtil {

    private RadiusUtil() {
    }

    /**
     * Returns the passed string as a byte array containing the
     * string in UTF-8 representation.
     *
     * @param str Java string
     * @return UTF-8 byte array
     */
    public static byte[] getUtf8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
            return str.getBytes();
        }
    }

    /**
     * Creates a string from the passed byte array containing the
     * string in UTF-8 representation.
     *
     * @param utf8 UTF-8 byte array
     * @return Java string
     */
    public static String getStringFromUtf8(byte[] utf8) {
        try {
            return new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            return new String(utf8);
        }
    }

    /**
     * Returns the byte array as a hex string in the format
     * "0x1234".
     *
     * @param data byte array
     * @return hex string
     */
    public static String getHexString(byte[] data) {
        StringBuilder hex = new StringBuilder("0x");
        if (data != null)
            for (int i = 0; i < data.length; i++) {
                String digit = Integer.toString(data[i] & 0x0ff, 16);
                if (digit.length() < 2)
                    hex.append('0');
                hex.append(digit);
            }
        return hex.toString();
    }

}
