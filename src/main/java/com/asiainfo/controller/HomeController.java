package com.asiainfo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Map<String, Object> model) {
		return "mail";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/code")
	public String code() {
		return "code";
	}

	@RequestMapping("/script")
	public String script() {
		return "script";
	}

	@RequestMapping("/mail")
	public String main() {
		return "mail";
	}

}
