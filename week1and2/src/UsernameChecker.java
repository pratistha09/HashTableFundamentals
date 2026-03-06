import java.util.*;
public class UsernameChecker {
    public final Map<String, Integer> usernameToUserId = new HashMap<>();
    public final Map<String, Integer> usernameAttempts = new HashMap<>();
    private String mostAttempted = "";
    private int maxAttempts = 0;
    public boolean checkAvailability(String username) {
        int attempts = usernameAttempts.getOrDefault(username, 0) + 1;
        usernameAttempts.put(username, attempts);
        if (attempts > maxAttempts) {
            maxAttempts = attempts;
            mostAttempted = username;
        }
        return !usernameToUserId.containsKey(username);
    }
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;
            if (!usernameToUserId.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }
        String dotVersion = username.replace("_", ".");
        if (!usernameToUserId.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }
        return suggestions;
    }
    public String getMostAttempted() {
        return mostAttempted + " (" + maxAttempts + " attempts)";
    }
    public void registerUser(String username, int userId) {
        usernameToUserId.put(username, userId);
    }

    public static void main(String[] args) {
        UsernameChecker system = new UsernameChecker();
        Scanner sc = new Scanner(System.in);
        system.registerUser("john_doe", 1001);
        system.registerUser("admin", 1002);
        system.registerUser("player1", 1003);
        system.registerUser("_yaasssqueen", 1004);
        system.registerUser("ashihihihihi", 1005);
        system.registerUser("v.rowr_neeka", 1006);
        system.registerUser("rawnak.ed", 1007);
        System.out.println("Enter username to check availability:");
        String username = sc.nextLine();
        boolean available = system.checkAvailability(username);
        if (available) {
            system.registerUser(username, new Random().nextInt(10000));
            System.out.println(username + " is available!");
            System.out.println(username + " registered successfully!");
        } else {
            System.out.println(username + " is already taken.");
            System.out.println("Suggested usernames: " + system.suggestAlternatives(username));
        }
        System.out.println("Most attempted username: " + system.getMostAttempted());

        sc.close();
    }
}