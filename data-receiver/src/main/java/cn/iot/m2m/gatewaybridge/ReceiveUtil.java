package cn.iot.m2m.gatewaybridge;

/**
 * @author: leihang@live.cn
 * @date: 2017-03-07 11:07
 * @description:
 */

public class ReceiveUtil {

    private static final int START_INDEX_WITH_86 = 2;

    private static final int START_INDEX_WITH_086 = 3;

    private static final int START_INDEX_WITH_0086 = 4;

    private ReceiveUtil() {
    }

    /**
     * 去掉msisdn开始位置的86、+86、086、0086
     * 来源于base.jar
     *
     * @param origin
     * @return
     */
    public static String formatMsisdn(String origin) {
        if (null == origin) {
            return null;
        }
        String msisdn = origin;
        if (msisdn.startsWith("86")) {
            msisdn = msisdn.substring(START_INDEX_WITH_86);
        } else if (msisdn.startsWith("+86") || msisdn.startsWith("086")) {
            msisdn = msisdn.substring(START_INDEX_WITH_086);
        } else if (msisdn.startsWith("0086")) {
            msisdn = msisdn.substring(START_INDEX_WITH_0086);
        }
        return msisdn;
    }
}
