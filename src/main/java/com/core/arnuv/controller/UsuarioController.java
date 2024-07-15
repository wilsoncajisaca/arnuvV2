package com.core.arnuv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	@GetMapping("listar")
	public String usersList() {
		return "/admin/users";
	}
}
