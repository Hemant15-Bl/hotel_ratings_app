package com.java.main.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingDto {

	private String ratingId;
	private String userId;
	private String hotelId;
	private int rating;
	private String feedback;
	
	private HotelDto hotel;
}
