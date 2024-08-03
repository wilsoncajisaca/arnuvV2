package com.core.arnuv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/content")
public class BlankController {
    @GetMapping("/welcome")
    public String index() {
        return "content/blank";
    }
}
