package com.moviebooking.movie_booking_monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedissonAutoConfigurationV2.class})
public class MovieBookingMonolithApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieBookingMonolithApplication.class, args);
	}

}
