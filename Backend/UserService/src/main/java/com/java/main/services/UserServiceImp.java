package com.java.main.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.java.main.services.exception.ResourceNotFoundException;
import com.java.main.services.payload.AppConstant;
import com.java.main.services.payload.HotelDto;
import com.java.main.services.payload.RatingDto;
import com.java.main.services.payload.UserDto;
import com.java.main.entites.Hotel;
import com.java.main.entites.Rating;
import com.java.main.entites.Role;
import com.java.main.entites.User;

import com.java.main.externalservices.HotelService;
import com.java.main.externalservices.RatingService;
import com.java.main.repository.RoleRepository;
import com.java.main.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

	@Autowired
	private UserRepository userRepository;


	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private RatingService ratingService;
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private ModelMapper modelMapper;

//	@Override
//	public UserDto addUser(UserDto userDto) {
//		User user = this.modelMapper.map(userDto, User.class);
//
//		String randomid = UUID.randomUUID().toString();
//
//		user.setUserId(randomid);
//		
//		user.setImageName("default-avatar.jpg");
//		
//		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
//
//		Role role = this.roleRepo.findById(AppConstant.ADMIN_USER).get();
//		user.getRoles().add(role);
//		User saved = userRepository.save(user);
//		return this.modelMapper.map(saved, UserDto.class);
//	}

	@Override
	public List<UserDto> getAllUser() {
		// TODO Auto-generated method stub
		List<User> users = userRepository.findAll();

		List<UserDto> list = users.stream().map(user -> (this.modelMapper.map(user, UserDto.class)))
				.collect(Collectors.toList());
		return list;
	}

	@Override
	public UserDto getUser(String id) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(id).orElseThrow(
				(() -> new ResourceNotFoundException("User with given id " + id + " is not found on server!!")));

		logger.info("User Name :{} ", user.getName());
		
		List<RatingDto> ratings = this.ratingService.getRating(user.getUserId());
		
//		List<RatingDto> ratings = Arrays.stream(ratingOfUser).toList();

		List<RatingDto> ratinglist = ratings.stream().map(rating -> {
			
			Hotel hotel = hotelService.gethotel(rating.getHotelId());
			
			HotelDto hotelDto = this.modelMapper.map(hotel, HotelDto.class);
			
			rating.setHotel(hotelDto);
			return rating;
		}).collect(Collectors.toList());

		List<Rating> list = ratinglist.stream().map(rate -> (this.modelMapper.map(rate, Rating.class)))
				.collect(Collectors.toList());
		user.setRating(list);
		return this.modelMapper.map(user, UserDto.class);
	}

	@Transactional
	@Override
	public UserDto register(UserDto userdto) {
		
		Optional<User> existsUser = this.userRepository.findByEmail(userdto.getEmail());
		if (existsUser.isPresent()) {
			System.out.println(">> ************ User ["+existsUser.get().getEmail()+"] already exists! **************");
			return this.modelMapper.map(existsUser.get(), UserDto.class);
		}

		if (userdto.getRoles() != null && !userdto.getRoles().isEmpty()) {
			List<Role> roles = userdto.getRoles().stream().map(r -> {
	            Role role = this.roleRepo.findByName(r.getName())
	                .orElseThrow(() -> new ResourceNotFoundException("Role Name ,"+ r.getName()));
	            return role;
	        }).collect(Collectors.toList());
			
			for(Role r: roles) {
				System.out.println("Role name from roles : "+r.getName());
				System.out.println("Role id from roles : "+r.getId());
				
			}
			userdto.getRoles().clear();
			userdto.getRoles().addAll(roles);
	    } else {
	        // Default Role (Hardcoded 501 for Normal User)
	        Role defaultRole = this.roleRepo.findById(501).orElseThrow(() -> new ResourceNotFoundException("Role ID 501"));
	        userdto.setRoles(new ArrayList<>());
	        userdto.getRoles().add(defaultRole);
	    }
		
		User user = this.modelMapper.map(userdto, User.class);

		String randomid = UUID.randomUUID().toString();
		user.setUserId(randomid);
		user.setImageName("default-avatar.jpg");
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		User save = this.userRepository.save(user);
		return this.modelMapper.map(save, UserDto.class);
	}

	@Override
	public String uploadImage(String path, MultipartFile file) throws IOException {
		
		String name = file.getOriginalFilename();

		String fileName = UUID.randomUUID().toString()+ "_" + name;
//		String fileName = randomId.concat(name.substring(name.lastIndexOf(".")));

//		String filePath = path + File.separator + fileName;
//
//		File file2 = new File(path);
//		if (!file2.exists()) {
//		this work only local env.
//		file2.mkdir();		
//		}
	
//		Files.copy(file.getInputStream(), Paths.get(filePath));
		
		Path rootLocation = Paths.get(path);
		if(!Files.exists(rootLocation)) {
			Files.createDirectories(rootLocation);
		}
		Files.copy(file.getInputStream(), rootLocation.resolve(fileName));
		return fileName;
	}

	@Override
	public InputStream getResource(String path, String fileName) throws FileNotFoundException {
		//Safety check when authdb has imageName or frontend send fileName as imageName
		if(fileName==null || fileName.equals("imageName") || fileName.isEmpty()) {
			fileName = "default-avatar.jpg";
		}
		
		String fullPath = path + File.separator + fileName;
		File file = new File(fullPath);
		
		if(!file.exists()) {
			logger.error("File not found: {}", fullPath);
			return new FileInputStream(path + File.separator + "default-avatar.jpg");
		}
		
		InputStream inputStream = new FileInputStream(fullPath);
		return inputStream;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		User user = userRepository.findById(userId).orElseThrow(
				(() -> new ResourceNotFoundException("User with given id " + userId + " is not found on server!!")));
		
		
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setContactNo(userDto.getContactNo());
		user.setAddress(userDto.getAddress());
		user.setImageName(userDto.getImageName());

		User save = this.userRepository.save(user);
		return this.modelMapper.map(save, UserDto.class);
	}

	@Override
	public UserDto getUserByUsername(String username) {
		User user = this.userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User with given id " + username + " is not found on server!!"));
		return this.modelMapper.map(user, UserDto.class);
	}

	@Transactional
	@Override
	public void removeUserData(String userId) {
		User user = userRepository.findById(userId).orElseThrow(
				(() -> new ResourceNotFoundException("User with given id " + userId + " is not found on server!!")));
		try {
			this.ratingService.removeRating(userId);
			System.out.println("Successfully cleared MongoDB ratings for user: " + userId);
		}catch (Exception e) {
	        throw new RuntimeException("Could not clear ratings from MongoDB. Delete aborted.");
		}
		
		// Clear join table references first
		user.getRoles().clear();
		this.userRepository.save(user);
		
		userRepository.delete(user);
	}

}
