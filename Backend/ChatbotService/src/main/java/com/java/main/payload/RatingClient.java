package com.java.main.payload;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "RATING-SERVICE")
public interface RatingClient {

	@GetMapping("/api/v3/rating/allratings")
	public List<RatingDto> getAllRating();
	
	@GetMapping("/api/v3/rating/hotels/{hotelId}")
	public List<RatingDto> getByHotel(@PathVariable String hotelId);
}
