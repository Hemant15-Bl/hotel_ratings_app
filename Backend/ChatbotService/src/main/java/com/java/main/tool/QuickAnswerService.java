package com.java.main.tool;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuickAnswerService {

	private final HotelTools tools;

    public Optional<String> tryAnswer(String question) {
        String s = question.toLowerCase();

        boolean ratingTopic = s.contains("rating") || s.contains("rated")
                || s.contains("average") || s.contains("score") || s.contains("stars");
        
        if (!ratingTopic) return Optional.empty();   // opinions go to the model

        
        List<HotelSummary> all = tools.summaries().stream()
                .filter(h -> h.reviewCount() > 0).toList();
        
        if (all.isEmpty()) return Optional.empty();

        
        // A specific hotel mentioned by name
        for (HotelSummary h : all) {
            if (mentions(s, h.name())) {
                return Optional.of("%s (%s) has an average rating of %.1f/10 from %d review%s."
                        .formatted(h.name(), h.location(), h.averageRating(),
                                h.reviewCount(), h.reviewCount() == 1 ? "" : "s"));
            }
        }

        
        if (s.contains("highest") || s.contains("best") || s.contains("top")) {
            HotelSummary h = all.stream()
                    .max(Comparator.comparingDouble(HotelSummary::averageRating)).get();
            return Optional.of("%s (%s) has the highest average rating: %.1f/10 from %d review%s."
                    .formatted(h.name(), h.location(), h.averageRating(),
                            h.reviewCount(), h.reviewCount() == 1 ? "" : "s"));
        }

        
        if (s.contains("lowest") || s.contains("worst")) {
            HotelSummary h = all.stream()
                    .min(Comparator.comparingDouble(HotelSummary::averageRating)).get();
            return Optional.of("%s (%s) has the lowest average rating: %.1f/10 from %d review%s."
                    .formatted(h.name(), h.location(), h.averageRating(),
                            h.reviewCount(), h.reviewCount() == 1 ? "" : "s"));
        }

        
        // Anything else about ratings: show the full ranking
        List<HotelSummary> ranked = all.stream()
                .sorted(Comparator.comparingDouble(HotelSummary::averageRating).reversed())
                .toList();
        StringBuilder sb = new StringBuilder("Average ratings, highest first:\n");
        for (int i = 0; i < ranked.size(); i++) {
            HotelSummary h = ranked.get(i);
            sb.append("%d. %s: %.1f/10 (%d review%s)\n".formatted(i + 1, h.name(),
                    h.averageRating(), h.reviewCount(), h.reviewCount() == 1 ? "" : "s"));
        }
        return Optional.of(sb.toString().trim());
    }

    
    // Matches the full hotel name, or any word in it longer than 3 letters ("Taj", "Radisson")
    private boolean mentions(String question, String hotelName) {
        String name = hotelName.toLowerCase();
        if (question.contains(name)) return true;
        return List.of(name.split("\\s+")).stream()
                .filter(w -> w.length() > 3 && !w.equals("hotel") && !w.equals("resort"))
                .anyMatch(question::contains);
    }
}
