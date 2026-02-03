package com.moviebooking.movie_booking_monolith.aop;

import com.moviebooking.movie_booking_monolith.annotation.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock lock) throws Throwable {
        String key = lock.key().isEmpty()
                ? joinPoint.getSignature().toShortString()
                : resolveKey(lock.key(), joinPoint.getArgs());
        RLock rLock = redissonClient.getLock(key);

        boolean acquired = rLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeUnit());
        if (!acquired) {
            log.warn("🔒 Lock not acquired: {}", key);
            return "Lock not acquired - seats busy";
        }

        try {
            log.info("🔒 Lock acquired: {}", key);
            return joinPoint.proceed();
        } finally {
            rLock.unlockAsync();
            log.info("🔓 Lock released: {}", key);
        }
    }

    private String resolveKey(String keyTemplate, Object[] args) {
        return keyTemplate.replaceAll("\\{}", args.length > 0 ? args[0].toString() : "");
    }
}
