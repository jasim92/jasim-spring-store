package com.jasim.store.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jasim.store.exceptions.RedisDataException;
import io.lettuce.core.RedisException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
public class RedisService {

    private RedisTemplate redisTemplate;

    public <T> T get(String key, TypeReference<T> typeReference){
        try {
            Object o =  redisTemplate.opsForValue().get(key);
            if (o == null) return null;

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(), typeReference);
        }catch (RedisException | JsonProcessingException e){
            throw new RedisDataException(e.getMessage());
        }

    }

    public void set(String key, Object o, Long ttl){
       try {
           ObjectMapper mapper = new ObjectMapper();
           // 1. Convert the List/Object to a JSON String manually
           String jsonString = mapper.writeValueAsString(o);

           // 2. Save the String to Redis (Add TimeUnit to be safe)
           redisTemplate.opsForValue().set(key, jsonString, ttl, TimeUnit.SECONDS);
       }catch (RedisException e){
           throw new RedisDataException(e.getMessage());
       } catch (JsonProcessingException e) {
           throw new RuntimeException(e);
       }
    }
}
