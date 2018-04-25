package com.zeed.user;

import com.zeed.generic.RestApiClient;
import com.zeed.user.config.DataSourceConfig;
import com.zeed.user.services.UserService;
import com.zeed.usermanagement.apimodels.UserModelApi;
import com.zeed.usermanagement.request.UserDetailsRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({DataSourceConfig.class, RestApiClient.class,UserService.class,UserDetailsRequest.class})
public class UserApplicationTests {

	@Autowired
	public UserDetailsRequest userDetailsRequest;
//
//	@Autowired
//	PasswordEncoder passwordEncoder;

	@Autowired
	UserService userService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void generatePassword (){

//		System.out.println(passwordEncoder.encode("secret"));

	}
	@Test
	public void testUserService () throws Exception {

		UserModelApi userModelApi = userService.getUserModelByUsername("hjbhjbjh");

		System.out.println(userModelApi);

	}
	@Test
	public void getUserInfo() throws Exception {
		UserModelApi userModelApi = userDetailsRequest.getManagedUserDetailsEntity("superuser");
		return;
	}


}
