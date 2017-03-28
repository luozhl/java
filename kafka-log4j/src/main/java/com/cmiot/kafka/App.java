package com.cmiot.kafka;

import org.apache.log4j.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        for (long i = 1L; i < 10000000000L; i++) {
            LOGGER.info("log4j producer [" + i + "]");
            Thread.sleep(1000);
        }
    }
}
