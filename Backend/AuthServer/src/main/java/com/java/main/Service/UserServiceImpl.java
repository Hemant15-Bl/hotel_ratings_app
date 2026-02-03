package com.java.main.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.main.dto.UserDto;
import com.java.main.exception.ResourceNotFoundException;
import com.java.main.externalServices.Role;
import com.java.main.externalServices.User;
import com.java.main.externalServices.UserService;
import com.java.main.repository.AuthRoleRepository;
import com.java.main.repository.AuthUserRepository;

@Service
public class UserServiceImpl {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthUserRepository authUserRepo;

	@Autowired
	private AuthRoleRepository authRoleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	// ---------- register user -------------------------
	@Transactional
	public UserDto registerUser(UserDto userDto) {
		Optional<User> existUser = this.authUserRepo.findByEmail(userDto.getEmail());
		if (existUser.isPresent()) {
			System.out.println("User is already registered with email: " + userDto.getEmail());
			return this.modelMapper.map(existUser.get(), UserDto.class);
		}

		// 1. Dynamic Role Assignment
		if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
			List<Role> roles = userDto.getRoles().stream().map(r -> {

				Role role = this.authRoleRepo.findByName(r.getName())
						.orElseThrow(() -> new ResourceNotFoundException("Role not found name: " + r.getName()));
				return role;
			}).collect(Collectors.toList());
//			userDto.getRoles().clear();
//			userDto.getRoles().addAll(roles);
			userDto.setRoles(new ArrayList<>(roles));
		} else {
			// Default Role (Hardcoded 501 for Normal User)
			Role defaultRole = this.authRoleRepo.findById(501)
					.orElseThrow(() -> new ResourceNotFoundException("Role ID 501"));
			System.out.println("RoleId: " + defaultRole.getId() + "  Name: " + defaultRole.getName());
			userDto.setRoles(new ArrayList<>());
			userDto.getRoles().add(defaultRole);
		}

		// 2. Save to User-Service via Feign
		UserDto user;
		try {
			user = this.userService.createUser(userDto);
		} catch (Exception e) {
			throw e;
		}

		// 3. Map to Auth User entity
		User profile = this.modelMapper.map(user, User.class);

		User authUser = this.modelMapper.map(userDto, User.class);

		// Setting random userId
		authUser.setUserId(profile.getUserId());
		authUser.setPassword(profile.getPassword());
		authUser.setImageName(profile.getImageName());
		this.authUserRepo.save(authUser); // save data in auth service db

		return this.modelMapper.map(authUser, UserDto.class);
	}
	// --------------------------------------------------------------------------------------------

	// --------------------- update user -----------------------------------
	@Transactional
	public UserDto update(String userId, UserDto userDto) {
		User updatedUser = this.modelMapper.map(this.userService.editUser(userId, userDto), User.class); // from
																											// user-service

		User authUser = this.modelMapper.map(userDto, User.class);

		User user = this.authUserRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found with Id: " + userId));

		user.setName(authUser.getName());
		user.setContactNo(authUser.getContactNo());
		user.setAddress(authUser.getAddress());
		user.setImageName(updatedUser.getImageName());

		return this.modelMapper.map(this.authUserRepo.save(user), UserDto.class);
	}
	// --------------------------------------------------------------------------------------------

	// --------------------- Delete user -----------------------------------
	@Transactional
	public void deleteUser(String userId) {
		User user = this.authUserRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		try {
			this.userService.removeData(userId); // user-feignclient from user-service
			System.out.println("User Delete from user-service ==> [" + user.getName() + "]");
		} catch (Exception e) {
			throw new RuntimeException("Could not clear user from authdb. Delete aborted");
		}

		// 1. Manually break the link in the join table
		user.getRoles().clear();
		authUserRepo.save(user); // This flushes the deletion of rows in 'user_role'

		// 2. Now perform the delete
		authUserRepo.delete(user);

	}
}
