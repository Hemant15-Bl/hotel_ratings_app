package com.java.main.payload;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "HOTEL-SERVICE")
public interface HotelClient {

	@GetMapping("/api/v2/hotel/{hotelId}")
	HotelDto gethotel(@PathVariable String hotelId);
	
	@GetMapping("/api/v2/hotel/allhotels")
	List<HotelDto> getAllHotels();
}
