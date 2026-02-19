package com.springcode.spring_ai.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private ChatClient chatClient;

    @Value("classpath:prompts/celebDetailPrompt.st")
    private Resource celebDetailPrompt;

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

    @GetMapping("/celeb")
    public String getCelebDetails(@RequestParam String name ) {
        String message ="""
                List the details of the Famous personality {name} along with their Carrier achievements.
                Please return the answer in html format.
                """;

        PromptTemplate template = new PromptTemplate(celebDetailPrompt);
        
        Prompt prompt = template.create(
        Map.of("name", name)
        ) ;
        // System.out.println(prompt);
        // return name;
        return chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }
    
}
