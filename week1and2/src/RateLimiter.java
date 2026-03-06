import java.util.concurrent.*;
import java.util.*;
class TokenBucket {
    private final int maxTokens;
    private final int refillRatePerHour; // tokens per hour
    private int tokens;
    private long lastRefillTime;
    public TokenBucket(int maxTokens) {
        this.maxTokens = maxTokens;
        this.refillRatePerHour = maxTokens; // refill all tokens every hour
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }
    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }
    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }
    public synchronized long getResetTimeMillis() {
        long elapsed = System.currentTimeMillis() - lastRefillTime;
        long remaining = TimeUnit.HOURS.toMillis(1) - elapsed;
        return remaining > 0 ? remaining : 0;
    }
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed >= TimeUnit.HOURS.toMillis(1)) {
            tokens = maxTokens;
            lastRefillTime = now;
        }
    }
}

public class RateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private final int maxRequestsPerHour;
    public RateLimiter(int maxRequestsPerHour) {
        this.maxRequestsPerHour = maxRequestsPerHour;
    }
    public String checkRateLimit(String clientId) {
        clients.putIfAbsent(clientId, new TokenBucket(maxRequestsPerHour));
        TokenBucket bucket = clients.get(clientId);
        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            long retryAfter = TimeUnit.MILLISECONDS.toSeconds(bucket.getResetTimeMillis());
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {
        clients.putIfAbsent(clientId, new TokenBucket(maxRequestsPerHour));
        TokenBucket bucket = clients.get(clientId);
        int used = maxRequestsPerHour - bucket.getRemainingTokens();
        long resetSec = TimeUnit.MILLISECONDS.toSeconds(bucket.getResetTimeMillis());
        return "{used: " + used + ", limit: " + maxRequestsPerHour + ", reset: " + resetSec + "s}";
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter(5);
        String clientId = "abc123";
        for (int i = 0; i < 7; i++) {
            System.out.println(limiter.checkRateLimit(clientId));
            Thread.sleep(500); // simulate small delay
        }
        System.out.println(limiter.getRateLimitStatus(clientId));
    }
}