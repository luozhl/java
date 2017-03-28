package cn.iot.m2m;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations={"classpath:applicationContext-app.xml"})
public class ReceiverApplication {

	private static final Logger logger = Logger.getLogger(ReceiverApplication.class);

	public static void main(String[] args) {
		 SpringApplication.run(ReceiverApplication.class, args);
	}
}
