package com.springcode.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class ImageController {
    private ChatModel chatModel;
    private ImageModel imageModel;

    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("image-to-text")
    public String describeImage() {
        String response = ChatClient.create(chatModel)
                            .prompt()
                            .user(userSpec -> 
                                userSpec.text("Describe the image")
                                        .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("images/dragon ball.png"))
                            )
                            .call()
                            .content();

        return response;
    }

    @GetMapping("image/{prompt}")
    public String getMethodName(@PathVariable String prompt) {
        ImageResponse imageResponse = imageModel.call( 
                                                    new ImagePrompt(prompt,
                                                    OpenAiImageOptions 
                                                            .builder()
                                                            .N(1)
                                                            .height(1024)
                                                            .width(1024)
                                                            .quality("hd")
                                                            .build()
                                                    )
                                        );
                                            
        return imageResponse.getResult().getOutput().getUrl();
    }
    
    
    
}
