package cn.iot.m2m.radius.util;

/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class RadiusException extends Exception {

    private static final long serialVersionUID = 2201204523946051388L;

    /**
     * Constructs a RadiusException with a message.
     *
     * @param message error message
     */
    public RadiusException(String message) {
        super(message);
    }


}
