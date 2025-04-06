package com.api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.service.ApiService;

@Controller
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/ask")
    public String showAskPage() {
        return "ask";
    }

    @PostMapping("/ask")
    public String getAnswer(@RequestParam("question") String question, Model model) {
        String answer = apiService.getAnswer(question);
        model.addAttribute("question", question);
        model.addAttribute("answer", answer);
        return "ask"; 
    }
}

