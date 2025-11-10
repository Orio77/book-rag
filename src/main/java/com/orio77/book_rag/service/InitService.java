package com.orio77.book_rag.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitService {

    public void init() {
        // init: load all books, process them into vectors, and store in the pinecone
        loadBooks();
        createDocuments();
        embedBooks();
        upsertToPinecone();
    }

    private void loadBooks() {
        // Load books from source
        log.info("Loading books");
    }

    private void createDocuments() {
        // Create documents from books
        log.info("Creating documents from books");
    }

    private void embedBooks() {
        // Process books into vectors
        log.info("Embedding books into vectors");
    }

    private void upsertToPinecone() {
        // Store vectors in Pinecone vector database
        log.info("Upserting vectors to Pinecone");
    }
}
