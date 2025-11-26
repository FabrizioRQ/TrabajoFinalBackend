package com.example.api.trabajofinal.security.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, Integer> attemptsByKey = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntilByKey = new ConcurrentHashMap<>();

    private final int MAX_ATTEMPTS = 5;
    private final long BLOCK_DURATION_MS = 15 * 60 * 1000;

    public boolean isBlocked(String key) {
        Long until = blockedUntilByKey.get(key);
        if (until == null) return false;
        if (System.currentTimeMillis() > until) {
            blockedUntilByKey.remove(key);
            attemptsByKey.remove(key);
            return false;
        }
        return true;
    }

    public void recordFailedAttempt(String key) {
        int attempts = attemptsByKey.getOrDefault(key, 0) + 1;
        attemptsByKey.put(key, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            blockedUntilByKey.put(key, System.currentTimeMillis() + BLOCK_DURATION_MS);
        }
    }

    public void resetAttempts(String key) {
        attemptsByKey.remove(key);
        blockedUntilByKey.remove(key);
    }
}
