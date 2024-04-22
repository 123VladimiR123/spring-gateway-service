package com.springgateway.springgateway.responseDistributor;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class CustomDistributor {

    private final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Semaphore> locks = new ConcurrentHashMap<>();

    public Semaphore getLock (String id) throws InterruptedException {
        if (responses.containsKey(id)) return new Semaphore(1);

        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        locks.put(id, semaphore);
        return semaphore;
    }

    @Async
    public void putResponse(String id, String email) {
        responses.put(id, email);
        if (locks.containsKey(id)) locks.get(id).release();
    }

    public String getResult(String id) {
        locks.remove(id);
        String response = responses.get(id);
        responses.remove(id);
        return response;
    }

}
