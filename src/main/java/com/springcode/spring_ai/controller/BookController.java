package com.springcode.spring_ai.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class BookController {

    private VectorStore vectorStore;
    private ChatClient chatClient;

    public BookController(VectorStore vectorStore, ChatClient.Builder builder) {
        this.vectorStore = vectorStore;
        // this.chatClient = chatClient
        //                     .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
        //                         .build()
        //                     )
                            // .build();
        this.chatClient = builder.build();
    }
    private String prompt = """
            Your task is to answer the questions about person. Use the information from the DOCUMENTS 
            section which contains resume to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
                        
            QUESTION:
            {input}
                        
            DOCUMENTS:
            {documents}
                        
            """;

    @GetMapping("book")
    public String isQuestion(@RequestParam String que) {

        return chatClient
                .prompt()
                .system("Return the answer in beautiful HTML format.")
                .user(que)
                .call()
                .content();
    }

    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    @GetMapping("resumeD")
    public String simplifyDoc(@RequestParam String que) {
        PromptTemplate template = new PromptTemplate(que);
        Map<String, Object> promptParams = new HashMap<>();

        promptParams.put("input", que);
        promptParams.put("documents", findSimilarData(que));
        

        return chatClient
                    .prompt(template.create(promptParams))
                    .call()
                    .content();
    }

    private String findSimilarData(String que) {
        SearchRequest request = SearchRequest.builder()
                                            .query(que)
                                            .topK(5)
                                            .build();
        List<Document> documents = vectorStore.similaritySearch(request);

        return documents
                    .stream()
                    .map(document-> document.getText().toString())
                    .collect(Collectors.joining());
        
    }
    
}
