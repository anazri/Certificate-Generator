package com.certificate.service;

import com.certificate.model.User;

public interface UserService {

	User loginUser(User user);
	User updateUser(User user);
	User registerUser(User user);
	User getUser(String email);
	
}
