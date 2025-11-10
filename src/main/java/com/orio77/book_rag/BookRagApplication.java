package com.orio77.book_rag;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

import com.orio77.book_rag.service.VectorStoreService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BookRagApplication {

	@Autowired
	private List<FileSystemResource> books;

	@Autowired
	private VectorStoreService vectorStoreService;

	public static void main(String[] args) {
		log.info("Starting Book RAG Application...");
		SpringApplication.run(BookRagApplication.class, args);
	}

	@Bean
	ToolCallbackProvider ragTools(VectorStoreService vectorStoreService) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(vectorStoreService)
				.build();
	}

	public void init() {
		AtomicInteger upsertedDocumentCount = new AtomicInteger(0);

		// Clear existing documents in the vector store
		log.info("Clearing existing documents in the vector store...");
		vectorStoreService.clear();

		books.forEach(book -> {
			PagePdfDocumentReader reader = new PagePdfDocumentReader(book);
			List<Document> documents = reader.get();
			log.info("Loaded {} documents from the {} book", documents.size(), book.getFilename());

			TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(1000).build();
			List<Document> chunks = splitter.split(documents);

			log.info("Split documents into {} chunks", chunks.size());

			vectorStoreService.upsertDocuments(chunks);

			log.info("Upserted {} documents into the vector store", chunks.size());
			upsertedDocumentCount.addAndGet(chunks.size());
		});

		log.info("Book RAG Application initialized successfully with {} documents upserted.",
				upsertedDocumentCount.get());

	}
}
