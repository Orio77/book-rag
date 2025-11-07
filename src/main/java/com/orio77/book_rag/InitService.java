package com.orio77.book_rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InitService {

    private static final Logger logger = LoggerFactory.getLogger(InitService.class);

    public void init() {
        // init: load all books, process them into vectors, and store in the pinecone
        loadBooks();
        createDocuments();
        embedBooks();
        upsertToPinecone();
    }

    private void loadBooks() {
        // Load books from source
        logger.info("Loading books");
    }

    private void createDocuments() {
        // Create documents from books
        logger.info("Creating documents from books");
    }

    private void embedBooks() {
        // Process books into vectors
        logger.info("Embedding books into vectors");
    }

    private void upsertToPinecone() {
        // Store vectors in Pinecone vector database
        logger.info("Upserting vectors to Pinecone");
    }
}
