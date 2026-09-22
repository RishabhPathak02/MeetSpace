package com.rishabh.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeetingApplication {
	public static void main(String[] args) {
		System.out.println("hell");
		SpringApplication.run(MeetingApplication.class, args);
	}
}
