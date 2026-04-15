package com.springcode.spring_ai.config;


import java.io.File;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

// @Configuration
public class VectorLoader {

    @Value("classpath:/indianConstitution.pdf")
    private Resource resourcePdf;

    @Bean
    SimpleVectorStore simpleVectorStore (EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        File vectorStoreFile = new File("src/main/resources/indianConstitution.json");

        if(vectorStoreFile.exists()) {
            System.out.println("File is already loaded");
            vectorStore.load(vectorStoreFile);
        }
        else {
            System.out.println("File is loading...");

            PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                                                .builder()
                                                .withPagesPerDocument(1)
                                                .build();

            PagePdfDocumentReader reader = new PagePdfDocumentReader(resourcePdf, config);
            
            var textSplitter = new TokenTextSplitter();

            List<Document> doc = textSplitter.apply(reader.get());

            vectorStore.add(doc);
            vectorStore.save(vectorStoreFile);
            System.out.println("Vector Store completed successfully");
        }

        return vectorStore;

    } 

}
