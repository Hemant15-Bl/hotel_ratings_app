package com.java.main.tool;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.java.main.payload.HotelClient;
import com.java.main.payload.RatingClient;
import com.java.main.payload.RatingDto;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class HotelTools {

	 @Autowired
	 private HotelClient hotels;     // Feign client to hotel-service
	 
	 @Autowired
	 private RatingClient ratings;   // Feign client to rating-service

	 private List<HotelSummary> summaries() {
		    Map<String, List<RatingDto>> byHotel = ratings.getAllRating().stream()
		        .collect(Collectors.groupingBy(RatingDto::getHotelId));

		    return hotels.getAllHotels().stream().map(h -> {
		      List<RatingDto> rs = byHotel.getOrDefault(h.getHotelId(), List.of());
		      double avg = rs.stream().mapToInt(RatingDto::getRating).average().orElse(0);
		      return new HotelSummary(h.getName(), h.getLocation(),
		          Math.round(avg * 100.0) / 100.0, rs.size());
		    }).toList();
		  }

		  @Tool(description = "Rank ALL hotels by average rating (out of 10), highest first, "
		      + "with review counts. Use for best/worst hotel, top hotels and comparisons.")
		  List<HotelSummary> rankHotelsByAverageRating() {
		    return summaries().stream()
		        .sorted(Comparator.comparingDouble(HotelSummary::averageRating).reversed())
		        .toList();
		  }

		  @Tool(description = "Get average rating (out of 10) and number of reviews for ONE hotel. "
		      + "Pass the hotel name; partial names like 'Taj' work.")
		  List<HotelSummary> getHotelSummary(
		      @ToolParam(description = "Hotel name or part of it") String hotelName) {
		    return summaries().stream()
		        .filter(s -> s.name().toLowerCase().contains(hotelName.toLowerCase()))
		        .toList();
		  }
}
