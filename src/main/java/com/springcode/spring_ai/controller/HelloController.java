package com.springcode.spring_ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springcode.spring_ai.model.Player;

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

        // PromptTemplate template = new PromptTemplate(message);
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

    @GetMapping("/sport")
    public String getSportDetail(@RequestParam String name) {
        String userMessage = """
                List the details of the Sport %s along with their Rules and Regulations.
                Show the details in the readable format. Please return answer in html format.
                """;
        String systemMessage = """
                You are a smart Virtual Assistant.
                Your task is to give the details about the Sports or something connected to Sports only.
                If someone ask about something else and you do not know
                Just say that you do not know the answer.
                """;

        UserMessage userPrompt = new UserMessage(String.format(userMessage, name));
        SystemMessage systemPrompt = new SystemMessage(systemMessage);

        Prompt prompt = new Prompt(List.of(userPrompt, systemPrompt));

        return chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/player")
    public List<Player> sportPlayers(@RequestParam String sport) {
        BeanOutputConverter<List<Player>> converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<Player>>() {});
        String message = """
                Generate a list of Career achievements for the sportsperson in sport: {sport} 
                Include the Player as the key and achievements as the value for it.
                {format}
                """;

        PromptTemplate template = new PromptTemplate(message);

        Prompt prompt = template.create(
            Map.of("sport",sport, "format", converter.getFormat())
        );

        return converter.convert(chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText());
                
    }
    
    
}
