package com.springcode.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private ChatClient chatClient;

    public HelloController (ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    @GetMapping
    public String prompt (@RequestParam String message) {
        return chatClient
                    .prompt(message+". Please return answer in html format.")
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
    }
    
}
