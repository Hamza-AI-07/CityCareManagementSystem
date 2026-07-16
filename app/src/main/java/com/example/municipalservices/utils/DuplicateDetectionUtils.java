package com.example.municipalservices.utils;

import com.example.municipalservices.models.ComplaintModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuplicateDetectionUtils {

    private static final double SIMILARITY_THRESHOLD = Constant.DUPLICATE_THRESHOLD;

    private static final List<String> STOPWORDS = Arrays.asList(
            "a", "an", "and", "the", "is", "are", "was", "were", "it", "its", "this",
            "that", "these", "those", "of", "in", "on", "at", "to", "for", "with",
            "by", "from", "as", "but", "or", "nor", "so", "yet", "i", "you", "he",
            "she", "we", "they", "me", "him", "her", "us", "them", "my", "your",
            "his", "her", "our", "their", "mine", "yours", "hers", "ours", "theirs"
    );

    public static class DuplicateResult {
        public boolean isDuplicate;
        public double similarityScore;
        public String matchedComplaintId;
        public String message;
        public String recommendedAction;

        public DuplicateResult(boolean isDuplicate, double similarityScore, String matchedComplaintId, String message, String recommendedAction) {
            this.isDuplicate = isDuplicate;
            this.similarityScore = similarityScore;
            this.matchedComplaintId = matchedComplaintId;
            this.message = message;
            this.recommendedAction = recommendedAction;
        }
    }

    private static String preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String processed = text.toLowerCase();
        processed = processed.replaceAll("[^a-zA-Z0-9\\s]", " ");
        processed = processed.replaceAll("\\s+", " ");
        String[] words = processed.split(" ");
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (!STOPWORDS.contains(word) && word.length() > 1) {
                filteredWords.add(word);
            }
        }
        return String.join(" ", filteredWords);
    }

    private static Map<String, Integer> getWordCount(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = text.split(" ");
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        return wordCount;
    }

    private static double calculateCosineSimilarity(String text1, String text2) {
        String processed1 = preprocessText(text1);
        String processed2 = preprocessText(text2);

        Map<String, Integer> count1 = getWordCount(processed1);
        Map<String, Integer> count2 = getWordCount(processed2);

        List<String> allWords = new ArrayList<>();
        allWords.addAll(count1.keySet());
        for (String word : count2.keySet()) {
            if (!allWords.contains(word)) {
                allWords.add(word);
            }
        }

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (String word : allWords) {
            int c1 = count1.getOrDefault(word, 0);
            int c2 = count2.getOrDefault(word, 0);
            dotProduct += c1 * c2;
            norm1 += c1 * c1;
            norm2 += c2 * c2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public static DuplicateResult checkForDuplicates(
            String newTitle,
            String newDescription,
            String newEmail,
            List<ComplaintModel> existingComplaints,
            Map<String, String> complaintIdMap
    ) {
        double maxSimilarity = 0;
        String matchedId = null;
        boolean isSameEmail = false;

        String combinedNewText = newTitle + " " + newDescription;

        for (ComplaintModel existing : existingComplaints) {
            String combinedExistingText = existing.getComplainTitle() + " " + existing.getComplainDescription();
            double similarity = calculateCosineSimilarity(combinedNewText, combinedExistingText);

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                matchedId = complaintIdMap != null ? complaintIdMap.get(String.valueOf(existing.getComplaintID())) : null;
                isSameEmail = existing.getComplainerEmail() != null && existing.getComplainerEmail().equalsIgnoreCase(newEmail);
            }
        }

        if (maxSimilarity >= SIMILARITY_THRESHOLD) {
            String message;
            if (isSameEmail) {
                message = "A similar complaint from this email already exists. Please check existing complaints.";
            } else {
                message = "A similar complaint already exists. Please check existing complaints.";
            }
            return new DuplicateResult(
                    true,
                    maxSimilarity,
                    matchedId,
                    message,
                    "review"
            );
        } else {
            return new DuplicateResult(
                    false,
                    maxSimilarity,
                    null,
                    "Complaint is unique and can be submitted.",
                    "submit"
            );
        }
    }
}
