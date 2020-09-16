package com.carlos.springboot.app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Rest {

	@GetMapping("/greeting")
	public String rest() {
		return "Hola Mundo";
	}
	
}
