package com.ipd.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ipd.Exception.ResourceNotFoundException;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.UserRepository;


@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
	
	@GetMapping("/me")
    public ResponseEntity<User> getCurrent() {
		User user = userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        return ResponseEntity.ok(user);
    }
	
    // Get All Admins
    @GetMapping("/admins")
    public List<User> getAllAdmins() {
        return userRepository.findByRole(Role.ADMIN);
    }

}
