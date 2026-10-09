package com.java.main.tool;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.java.main.payload.HotelClient;
import com.java.main.payload.HotelDto;
import com.java.main.payload.RatingClient;
import com.java.main.payload.RatingDto;

@Component
public class ReviewIngestor {

	@Autowired
	private RatingClient ratings;

	@Autowired
	private HotelClient hotels;

	@Autowired
	private VectorStore store;

	private static Logger logger = LoggerFactory.getLogger(ReviewIngestor.class);
	
	// Same input always gives the same UUID, so re-ingesting overwrites instead of duplicating
	private static String uuid(String key) {
		return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
	}

	@EventListener(ApplicationReadyEvent.class) // or @Scheduled for refreshes
	void ingest() {
		for(int attempt = 1; attempt<=10; attempt++) {
			try {
				doIngest();
				logger.info("Ingestion is finished!");
				return;
			}catch (Exception e) {
				logger.warn("Ingestion attempt {} finish: {}", attempt, e.getMessage());
				try {
					Thread.sleep(15_000);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
		logger.error("Ingestion failed after 10 attempts");
	}
	
	public void doIngest() {
		List<Document> docs = new ArrayList<>();

		// 1. One document per hotel (descriptions, locations)
		Map<String, HotelDto> hotelById = new HashMap<>();
		for (HotelDto h : hotels.getAllHotels()) {
			hotelById.put(h.getHotelId(), h);
			docs.add(new Document(
					uuid("hotel-" + h.getHotelId()),
					"Hotel: %s. Location: %s. About: %s".formatted(h.getName(), h.getLocation(), h.getAbout()),
					Map.of("type", "hotel", "hotelId", h.getHotelId())));
		}

		// 2. One document per rating/review
		for (RatingDto r : ratings.getAllRating()) {
			HotelDto h = r.getHotel() != null ? r.getHotel() : hotelById.get(r.getHotelId());
			String name = h != null ? h.getName() : "Unknown hotel";
			String place = h != null ? h.getLocation() : "";

			docs.add(new Document(
					uuid("review-" + r.getRatingId()),
					"Hotel: %s (%s). Rating: %d/10. Review: %s".formatted(name, place, r.getRating(), r.getFeedback()),
					Map.of("type", "review", "hotelId", r.getHotelId(), "rating", r.getRating())));
		}

		store.add(docs); // embeds with nomic-embed-text, then stores in pgvector
	}
}
