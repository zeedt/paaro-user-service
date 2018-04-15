package com.zeed.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApplicationTests {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	public void contextLoads() {
	}

	@Test
	public void generatePassword (){

		System.out.println(passwordEncoder.encode("password"));

	}



}
