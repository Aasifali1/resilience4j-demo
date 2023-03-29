package com.knoldus.resilience4jdemo.service;

import com.knoldus.resilience4jdemo.model.User;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private static final String SERVICE_NAME = "user-service";
    @Autowired
    RestTemplate restTemplate;

    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "usersFallback")
    @Retry(name = SERVICE_NAME, fallbackMethod = "retryUsersFallback")
    @Bulkhead(name = SERVICE_NAME)
    @RateLimiter(name = SERVICE_NAME)
    @TimeLimiter(name = SERVICE_NAME)
    public List<User> getUsers() {
        List<User> users = restTemplate.getForObject("https://gorest.co.in/public/v2/userss", List.class);
        return users;
    }

    public List<User> usersFallback(Exception ex){
        return Arrays.asList(
                new User(1, "name", "Service is down", "email", "gender"),
                new User(1, "name","Service is down", "email", "gender")
        );
    }

    public List<User> retryUsersFallback(Exception ex){
        return Arrays.asList(
                new User(1, "retry", "Service is down", "email", "gender"),
                new User(1, "retry","Service is down", "email", "gender")
        );
    }
}
