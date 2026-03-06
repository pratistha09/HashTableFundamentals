import java.util.*;
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord = false;
    String query = null;
    int frequency = 0;
}

public class AutocompleteSystem {
    private TrieNode root = new TrieNode();
    private final int TOP_K = 10;
    public void insertQuery(String query, int freq) {
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isWord = true;
        node.query = query;
        node.frequency += freq;
    }
    public List<String> getSuggestions(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }
        PriorityQueue<TrieNode> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(a -> a.frequency)
        );
        dfs(node, minHeap);
        List<String> result = new ArrayList<>();
        List<TrieNode> temp = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            temp.add(minHeap.poll());
        }
        Collections.reverse(temp);
        for (TrieNode n : temp) result.add(n.query + " (" + n.frequency + " searches)");
        return result;
    }
    private void dfs(TrieNode node, PriorityQueue<TrieNode> heap) {
        if (node.isWord) {
            heap.offer(node);
            if (heap.size() > TOP_K) heap.poll();
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, heap);
        }
    }
    public void updateFrequency(String query) {
        insertQuery(query, 1);
    }

    public static void main(String[] args) {
        AutocompleteSystem ac = new AutocompleteSystem();
        ac.insertQuery("java tutorial", 1234567);
        ac.insertQuery("javascript", 987654);
        ac.insertQuery("java download", 456789);
        ac.insertQuery("java 21 features", 1234);
        ac.insertQuery("javac compiler", 54321);
        ac.insertQuery("java tutorial for beginners", 654321);
        System.out.println("Suggestions for prefix 'jav':");
        List<String> suggestions = ac.getSuggestions("jav");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println((i + 1) + ". " + suggestions.get(i));
        }
        ac.updateFrequency("java 21 features");
        ac.updateFrequency("java 21 features");
        ac.updateFrequency("java 21 features");
        System.out.println("\nAfter updating frequency of 'java 21 features':");
        suggestions = ac.getSuggestions("jav");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println((i + 1) + ". " + suggestions.get(i));
        }
    }
}