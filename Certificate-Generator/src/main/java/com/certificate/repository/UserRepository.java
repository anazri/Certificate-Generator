package com.certificate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.certificate.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findUserByEmail(String email);
	
}
