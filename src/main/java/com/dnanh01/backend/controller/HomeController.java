package com.dnanh01.backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class HomeController {
	@GetMapping("/api/index")
	public ResponseEntity<?> trangChu(){
		return ResponseEntity.ok("Trang chủ");
	}
	@GetMapping("/admin")
	public ResponseEntity<?> admin(Principal principal){
		return ResponseEntity.ok("Xin chào admin ");
	}
	
	@GetMapping("/user")
	public ResponseEntity<?> usre(Principal principal){
		return ResponseEntity.ok("Xin chào user ");
	}
}
 
