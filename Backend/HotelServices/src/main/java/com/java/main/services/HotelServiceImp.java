package com.java.main.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.main.entities.Hotel;
import com.java.main.exception.ResourceNotFound;
import com.java.main.payload.HotelDto;
import com.java.main.repository.HotelRepository;

@Service
public class HotelServiceImp implements HotelService{
	
	@Autowired
	private HotelRepository horepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public HotelDto addhotel(HotelDto hotelDto) {
		Hotel hotel = this.modelMapper.map(hotelDto, Hotel.class);
		String generateid = UUID.randomUUID().toString();
		hotel.setHotelId(generateid);
		Hotel saved = horepository.save(hotel);
		return this.modelMapper.map(saved, HotelDto.class);
	}

	@Override
	public List<HotelDto> getAllHote() {

		List<Hotel> hotels = horepository.findAll();
		List<HotelDto> list = hotels.stream().map(hotel -> (this.modelMapper.map(hotel, HotelDto.class))).collect(Collectors.toList());
		return list;
	}

	@Override
	public HotelDto getHotel(String hotelId) {
		
		Hotel hotel = horepository.findById(hotelId).orElseThrow(() -> new ResourceNotFound("Hotel With Id Not Found !!"));
		return this.modelMapper.map(hotel, HotelDto.class);
	}

	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		
		String name = file.getOriginalFilename();
		
		String fileName = UUID.randomUUID().toString()+ "_"+ name;
		
//		String fileName = randomId.concat(name.substring(name.lastIndexOf(".")));
		
//		String filePath = path + File.separator+ fileName;
//		
//		File file2 = new File(path);
//		if(!file2.exists()) {
//			file2.mkdir();
//		}
//		
//		Files.copy(file.getInputStream(), Paths.get(filePath));
		Path rootLocation = Paths.get(path);
		if (!Files.exists(rootLocation)) {
			Files.createDirectories(rootLocation);
		}
		
		Files.copy(file.getInputStream(), rootLocation.resolve(fileName));
		return fileName;
	}

	@Override
	public InputStream getResource(String path, String fileName) throws FileNotFoundException {
		String fullPath = path + File.separator+fileName;
		InputStream inputStream = new FileInputStream(fullPath);
		return inputStream;
	}

	@Override
	public HotelDto updateHotel(String hotelId, HotelDto hotelDto) {
		HotelDto hotel = getHotel(hotelId);
		
		hotel.setName(hotelDto.getName());
		hotel.setAbout(hotelDto.getAbout());
		hotel.setLocation(hotelDto.getLocation());
		hotel.setImageName(hotelDto.getImageName());
		
		Hotel saved = this.horepository.save(this.modelMapper.map(hotel, Hotel.class));
		return this.modelMapper.map(saved, HotelDto.class);
	}

	@Override
	public void removeHotel(String hotelId) {
		this.horepository.deleteById(hotelId);
	}

}
