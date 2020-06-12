package com.intland.gabordicsotest.tree.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping("/")
	public String getIndex(Map<String, Object> model) {
		return "index";
	}
}
