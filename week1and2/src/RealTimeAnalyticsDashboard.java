import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class PageViewEvent {
    String url;
    String userId;
    String source;
    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {
    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();
    private final int TOP_N = 10;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public RealTimeAnalyticsDashboard() {
        scheduler.scheduleAtFixedRate(this::printDashboard, 5, 5, TimeUnit.SECONDS);
    }

    public void processEvent(PageViewEvent event) {
        pageViews.merge(event.url, 1, Integer::sum);
        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        trafficSources.merge(event.source.toLowerCase(), 1, Integer::sum);
    }

    public void printDashboard() {
        System.out.println("\n=== Real-Time Dashboard ===");
        List<Map.Entry<String, Integer>> topPages = pageViews.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(TOP_N)
                .collect(Collectors.toList());
        System.out.println("Top Pages:");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }
        int totalSource = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            String source = entry.getKey();
            double percent = totalSource == 0 ? 0 : (entry.getValue() * 100.0 / totalSource);
            System.out.println(capitalize(source) + ": " + String.format("%.1f", percent) + "%");
        }
    }
    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        RealTimeAnalyticsDashboard dashboard = new RealTimeAnalyticsDashboard();
        String[] urls = {"/article/breaking-news", "/sports/championship", "/tech/ai-update"};
        String[] users = {"user_1", "user_2", "user_3", "user_4", "user_5"};
        String[] sources = {"google", "facebook", "direct"};
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            String url = urls[r.nextInt(urls.length)];
            String user = users[r.nextInt(users.length)];
            String source = sources[r.nextInt(sources.length)];
            dashboard.processEvent(new PageViewEvent(url, user, source));
            Thread.sleep(20);
        }
        Thread.sleep(6000);
        dashboard.shutdown();
    }
}