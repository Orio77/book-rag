package com.orio77.book_rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.pinecone.clients.Pinecone;

@Configuration
public class PineconeBean {

    @Value("${spring.ai.vectorstore.pinecone.apiKey}")
    private String apiKey;

    @Bean
    Pinecone getPineconeClient() {
        return new Pinecone.Builder(apiKey)
                .build();
    }
}
