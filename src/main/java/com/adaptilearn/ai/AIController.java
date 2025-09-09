package com.adaptilearn.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/hint")
    public ResponseEntity<Map<String,String>> hint(@RequestBody Map<String,String> body) {
        String prompt = body.getOrDefault("prompt", "");
        String hint = aiService.getHint(prompt);
        return ResponseEntity.ok(Map.of("hint", hint));
    }
}
