package com.core.arnuv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class BlankController {
    @GetMapping("/home")
    public String index() {
        return "/content-page/blank";
    }
}
