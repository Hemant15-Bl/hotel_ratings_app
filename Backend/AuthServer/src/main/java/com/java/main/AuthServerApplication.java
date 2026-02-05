package com.java.main;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.java.main.Service.UserServiceImpl;
import com.java.main.dto.UserDto;
import com.java.main.externalServices.Role;
import com.java.main.repository.AuthRoleRepository;
import com.java.main.repository.AuthUserRepository;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthServerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

	@Autowired
	private AuthRoleRepository authRoleRepo;

	@Autowired
	private AuthUserRepository authUserRepo;

	@Autowired
	private UserServiceImpl authUserServiceImpl;


	@Override
	public void run(String... args) throws Exception {

		Role role = new Role();
		role.setId(501);
		role.setName("ROLE_NORMAL");

		Role role2 = new Role();
		role2.setId(502);
		role2.setName("ROLE_ADMIN");

		try {
			List<Role> list = List.of(role, role2);
			List<Role> roles = this.authRoleRepo.saveAll(list);
			
			this.authRoleRepo.flush();

			// 2. Initial Admin Check
			String adminEmail = "aman@gmail.com";

			if (authUserRepo.findByEmail(adminEmail).isEmpty()) {
				UserDto admin = new UserDto();
				admin.setName("Aman Barole");
				admin.setEmail(adminEmail);
				admin.setPassword("admin123"); // Set a default password
				admin.setImageName("default-avatar.jpg");
				admin.setAddress("Chandigarh, India");
				admin.setContactNo("9131297884");

				// Attach the roles to the DTO
				// Your register method uses r.getName() from this list to look them up
				admin.setRoles(new ArrayList<>(List.of(role2)));

				// Small delay to ensure User-Service is ready for the Feign call
				int attempts = 0;
				while (attempts < 18) {
					try {
						// 3. Register the user via Service (this handles password encoding and UUID)
						UserDto registerUser = this.authUserServiceImpl.registerUser(admin);
						System.out.println(">> Auth-Server: Successfully registered Admin via Feign. Email: "+registerUser.getEmail());
						break;
					} catch (Exception e) {
						attempts++;
						System.out.println(">> Auth-Server: User-Service not ready. Retrying (" + attempts + "/5)...");
						System.out.println("Error message: "+e.getMessage());
						e.printStackTrace();
						Thread.sleep(15000); // Wait 9 seconds
					}
				}

			} else {
				System.out.println(">> Info: Admin user already exists. Skipping initialization.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
