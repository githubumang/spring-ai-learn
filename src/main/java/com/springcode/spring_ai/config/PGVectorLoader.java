package com.springcode.spring_ai.config;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PGVectorLoader {

    @Value("classpath:/resume.pdf")
    private Resource resourcePdf;
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    public PGVectorLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClient
                            .sql("SELECT COUNT(*) FROM vector_store")
                            .query(Integer.class)
                            .single();

        System.out.println("No of Documens in PG Vector Store = "+count);

        if(count == 0) {
            System.out.println("Initializing PG Vector Store");
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                                                .builder()
                                                .withPagesPerDocument(1)
                                                .build();

            PagePdfDocumentReader reader = new PagePdfDocumentReader(resourcePdf, config);

            var textSplitter = new TokenTextSplitter();

            vectorStore.accept(textSplitter.apply(reader.get()));
            System.out.println("Application is started and ready to set");
        }
    }


}
