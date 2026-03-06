import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;
    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
    }
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class HashMapFundamentals {
    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    int MAX_CACHE_SIZE = 5;
                    return size() > MAX_CACHE_SIZE;
                }
            };
    private int cacheHits = 0;
    private int cacheMisses = 0;

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);
        if (entry != null) {
            if (!entry.isExpired()) {
                cacheHits++;
                System.out.println("Cache HIT → " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                System.out.println("Cache EXPIRED → removing old entry");
                cache.remove(domain);
            }
        }
        cacheMisses++;
        String ip = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, ip, 5)); // TTL = 5 seconds
        System.out.println("Cache MISS → Querying upstream DNS");
        return ip;
    }

    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + r.nextInt(255);
    }

    public void getCacheStats() {
        int total = cacheHits + cacheMisses;
        double hitRate = total == 0 ? 0 : (cacheHits * 100.0) / total;
        System.out.println("\nCache Statistics");
        System.out.println("-----------------");
        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {
        HashMapFundamentals dns = new HashMapFundamentals();
        System.out.println("Resolving google.com");
        System.out.println("IP Address: " + dns.resolve("google.com"));
        System.out.println("\nResolving google.com again");
        System.out.println("IP Address: " + dns.resolve("google.com"));
        System.out.println("\nWaiting for TTL to expire...");
        Thread.sleep(6000);
        System.out.println("\nResolving google.com after TTL");
        System.out.println("IP Address: " + dns.resolve("google.com"));

        dns.getCacheStats();
    }
}