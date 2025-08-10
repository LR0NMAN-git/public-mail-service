package com.jtcwzy.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailServiceApplication.class, args);
	}

}
