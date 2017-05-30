package com.certificate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.certificate.model.User;
import com.certificate.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public User loginUser(User user) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User temp = userRepository.findUserByEmail(user.getEmail());
		if(temp != null && passwordEncoder.matches(user.getPassword(), temp.getPassword()))
			return temp;
		return null;
	}

	@Override
	@Transactional
	public User updateUser(User user) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User temp = userRepository.findOne(user.getId());
		temp.setEmail(user.getEmail());
		temp.setName(user.getEmail());
		temp.setPassword(passwordEncoder.encode(user.getPassword()));
		temp.setSurname(user.getSurname());
		return userRepository.save(temp);
	}

	@Override
	@Transactional
	public User registerUser(User user) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

}
