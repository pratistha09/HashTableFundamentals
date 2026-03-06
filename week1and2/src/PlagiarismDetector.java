import java.util.*;
public class PlagiarismDetector {
    private static final int N = 5;
    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private Map<String, List<String>> documentNgrams = new HashMap<>();
    public List<String> extractNgrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }
            ngrams.add(gram.toString().trim());
        }
        return ngrams;
    }

    public void addDocument(String documentId, String text) {
        List<String> ngrams = extractNgrams(text);
        documentNgrams.put(documentId, ngrams);
        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(documentId);
        }
        System.out.println("Document added: " + documentId +
                " (Extracted " + ngrams.size() + " n-grams)");
    }

    public void analyzeDocument(String documentId) {
        List<String> ngrams = documentNgrams.get(documentId);
        Map<String, Integer> matchCount = new HashMap<>();
        for (String gram : ngrams) {
            Set<String> docs = ngramIndex.getOrDefault(gram, new HashSet<>());
            for (String doc : docs) {
                if (!doc.equals(documentId)) {
                    matchCount.put(doc, matchCount.getOrDefault(doc, 0) + 1);
                }
            }
        }
        System.out.println("\nAnalyzing document: " + documentId);
        System.out.println("Total n-grams: " + ngrams.size());
        for (String doc : matchCount.keySet()) {
            int matches = matchCount.get(doc);
            double similarity = (matches * 100.0) / ngrams.size();
            System.out.println("Matches with " + doc + ": " + matches +
                    " → Similarity: " + String.format("%.2f", similarity) + "%");
            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED with " + doc);
            }
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();
        String essay1 = "Artificial intelligence is transforming the world. "
                + "Machine learning and deep learning are popular technologies.";
        String essay2 = "Artificial intelligence is transforming the world. "
                + "Machine learning and deep learning are widely used today.";
        String essay3 = "Cloud computing provides scalable infrastructure "
                + "for modern software applications.";
        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay1 + " " + essay2);

        detector.analyzeDocument("essay_123.txt");
    }
}