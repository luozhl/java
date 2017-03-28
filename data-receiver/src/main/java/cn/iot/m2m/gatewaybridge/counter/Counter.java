package cn.iot.m2m.gatewaybridge.counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 4个计数器
 * 1.每分钟
 * 2.每5分钟
 * 3.每1小时
 * 4.每天
 */
public class Counter {

    //每分钟接收数据总数
    private static AtomicLong receiveNum1m = new AtomicLong(0);
    //每5分钟接收数据总数
    private static AtomicLong receiveNum5m = new AtomicLong(0);
    //每1小时接收数据总数
    private static AtomicLong receiveNum60m = new AtomicLong(0);
    //每天接收数据总数
    private static AtomicLong receiveNumDay = new AtomicLong(0);

    private Counter() {
    }

    //线程安全计数器,累加:每分钟接收数据总数
    public static void countReceiveNum1m() {
        for (; ; ) {
            long i = receiveNum1m.get();
            boolean suc = receiveNum1m.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    //获取当前计数器值，并将计数器清0:每分钟接收数据总数
    public static long getAndResetReceiveNum1m() {
        for (; ; ) {
            long current = receiveNum1m.get();
            if (receiveNum1m.compareAndSet(current, 0)) {
                return current;
            }
        }
    }


    //线程安全计数器,累加:每5分钟接收数据总数
    public static void countReceiveNum5m() {
        for (; ; ) {
            long i = receiveNum5m.get();
            boolean suc = receiveNum5m.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    //获取当前计数器值，并将计数器清0:每5分钟接收数据总数
    public static long getAndResetReceiveNum5m() {
        for (; ; ) {
            long current = receiveNum5m.get();
            if (receiveNum5m.compareAndSet(current, 0)) {
                return current;
            }
        }
    }


    //线程安全计数器,累加:每1小时接收数据总数
    public static void countReceiveNum60m() {
        for (; ; ) {
            long i = receiveNum60m.get();
            boolean suc = receiveNum60m.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    //获取当前计数器值，并将计数器清0:0:每1小时接收数据总数
    public static long getAndResetReceiveNum60m() {
        for (; ; ) {
            long current = receiveNum60m.get();
            if (receiveNum60m.compareAndSet(current, 0)) {
                return current;
            }
        }
    }


    //线程安全计数器,累加:每天接收数据总数
    public static void countReceiveNumDay() {
        for (; ; ) {
            long i = receiveNumDay.get();
            boolean suc = receiveNumDay.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    //线程安全计数器,清0:每天接收数据总数
    public static long getAndResetReceiveNumDay() {
        for (; ; ) {
            long current = receiveNumDay.get();
            if (receiveNumDay.compareAndSet(current, 0)) {
                return current;
            }
        }
    }

}
