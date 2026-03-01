package com.springcode.spring_ai.controller;

import org.springframework.http.HttpHeaders;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class AudioController {
    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    public AudioController(OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel, OpenAiAudioSpeechModel openAiAudioSpeechModel) {
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
    }

    @GetMapping("audio-to-text") 
    public String getAudioText() {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions
                                                        .builder()
                                                        .language("hi")
                                                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.SRT) // Use the correct enum value
                                                        .temperature(0.5f)
                                                        .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(new ClassPathResource("audio/audio.m4a"), options);

        return openAiAudioTranscriptionModel.call(prompt).getResult().getOutput();
    }

    @GetMapping("text-to-audio/{text}")
    public ResponseEntity<ByteArrayResource> generateAudio(@PathVariable String text) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                                                .model(OpenAiAudioApi.TtsModel.GPT_4_O_MINI_TTS.getValue())
                                                .responseFormat(AudioResponseFormat.MP3)
                                                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY.getValue())
                                                .speed(1.0)
                                                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);

        byte[] output = openAiAudioSpeechModel.call(prompt).getResult().getOutput();

        ByteArrayResource resource = new ByteArrayResource(output);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("SpringAiLearn.mp3").build().toString())
                .body(resource);
        
    }
    
}
