package com.orio77.book_rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookRagApplication {

	private static final Logger logger = LoggerFactory.getLogger(BookRagApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Book RAG Application...");

		SpringApplication.run(BookRagApplication.class, args);

		logger.info("Book RAG Application started successfully");
		logger.debug("Debug mode is enabled - this will help with troubleshooting");

		try {
			// init: load all books, process them into vectors, and store in the pinecone
			// vector database
			logger.info("Initializing vector database operations...");

			// Tool1: Query pinecone vector database with user question vector to get top-k
			// similar books
			logger.info("Setting up query capabilities for vector database");

		} catch (Exception e) {
			logger.error("Error during application initialization", e);
		}

		logger.info("Book RAG Application setup completed");
	}

}
