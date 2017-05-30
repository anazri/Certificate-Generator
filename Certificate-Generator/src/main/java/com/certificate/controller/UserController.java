package com.certificate.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.certificate.model.User;
import com.certificate.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/login")
	@ResponseBody
	public User loginUser(@RequestBody User user, HttpSession session){
		User loggedUser = userService.loginUser(user);
		session.setAttribute("user", loggedUser);
		return loggedUser;
	}
	
	@PostMapping("/update")
	@ResponseBody
	public User updateUser(@RequestBody User user, HttpSession session){
		User editedUser = userService.updateUser(user);
		session.setAttribute("user", editedUser);
		return editedUser;
	}
	
	@PostMapping("/register")
	@ResponseBody
	public User registerUser(@RequestBody User user){
		return userService.registerUser(user);
	}
	
	@GetMapping("/logout")
	public void logoutUser(HttpSession session){
		session.invalidate();
	}
	
}
