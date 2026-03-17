package com.springcode.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
 import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class BookController {

    private VectorStore vectorStore;
    private ChatClient chatClient;

    public BookController(VectorStore vectorStore, ChatClient.Builder chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient
                            .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                .build()
                            )
                            .build();
    }

    @GetMapping("book")
    public String isQuestion(@RequestParam String que) {

        return chatClient
                .prompt()
                .system("Return the answer in beautiful HTML format.")
                .user(que)
                .call()
                .content();
    }
    
}
