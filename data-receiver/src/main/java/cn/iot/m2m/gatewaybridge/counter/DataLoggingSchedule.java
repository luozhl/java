package cn.iot.m2m.gatewaybridge.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时输出接收到的数据
 */
@Component
public class DataLoggingSchedule {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private String loggerStr = "报文接收程序：当前{}分钟接收数据量:{}";

    /**
     * 每1分钟运行
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void receiveDataNumIn1Min() {
        log.info(loggerStr, 1, Counter.getAndResetReceiveNum1m());
    }

    /**
     * 每5分钟运行
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void receiveDataNumIn5Min() {
        log.info(loggerStr, 5, Counter.getAndResetReceiveNum5m());
    }

    /**
     * 每60分钟运行
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void receiveDataNumIn60Min() {
        log.info(loggerStr, 60, +Counter.getAndResetReceiveNum60m());
    }

    /**
     * 每天运行
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void receiveDataNumInDay() {
        log.info("报文接收程序：当天接收数据量:{}", Counter.getAndResetReceiveNumDay());
    }
}
