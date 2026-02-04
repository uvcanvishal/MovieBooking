package com.moviebooking.movie_booking_monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;

@SpringBootApplication(exclude = {RedissonAutoConfigurationV2.class})
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableCaching
public class MovieBookingMonolithApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieBookingMonolithApplication.class, args);
	}

}
