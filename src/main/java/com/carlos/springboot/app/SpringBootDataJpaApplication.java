package com.carlos.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.carlos.springboot.app.models.service.IUploadFilesService;

@SpringBootApplication
public class SpringBootDataJpaApplication implements CommandLineRunner {
	
	@Autowired
	IUploadFilesService uploadFileService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncode;
 
	public static void main(String[] args) {
		SpringApplication.run(SpringBootDataJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		uploadFileService.deleteAll();
		uploadFileService.init();
		
		String password = "12345";
		
		for(int i = 0; i < 2; i ++) {
			String bcryptPassword = passwordEncode.encode(password);
			System.out.println(bcryptPassword);
		}
		
	}

}
