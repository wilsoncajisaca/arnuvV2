package com.core.arnuv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/mascota")
public class MascotaController {

	
	    @GetMapping("/listar")
	    public String login() {
	        return "/admin/mascotas";
	    }
}
