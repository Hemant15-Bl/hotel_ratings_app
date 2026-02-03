package com.java.main;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.java.main.entites.Role;
import com.java.main.repository.RoleRepository;
import com.java.main.repository.UserRepository;
import com.java.main.services.UserService;
//import com.java.main.repository.UserRepository;
import com.java.main.services.payload.AppConstant;
import com.java.main.services.payload.UserDto;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SpringProject13Eureka1Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringProject13Eureka1Application.class, args);
	}

	@Autowired
	private RoleRepository roleRepo;

	@Override
	public void run(String... args) throws Exception {

		try {
			Role role1 = new Role();
			role1.setId(AppConstant.NORMAL_USER);
			role1.setName("ROLE_NORMAL");

			Role role2 = new Role();
			role2.setId(AppConstant.ADMIN_USER);
			role2.setName("ROLE_ADMIN");

			List<Role> roles = List.of(role1, role2);
			List<Role> result = this.roleRepo.saveAll(roles);
			result.forEach(r -> {
				System.out.println(r.getName());
			});

		} catch (Exception e) {
			System.err.println(">> Error during data initialization: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
