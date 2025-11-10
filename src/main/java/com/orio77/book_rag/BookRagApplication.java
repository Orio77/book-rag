package com.orio77.book_rag;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.orio77.book_rag.service.InitService;
import com.orio77.book_rag.service.VectorStoreService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BookRagApplication {

	@Autowired
	private InitService initService;

	public static void main(String[] args) {
		log.info("Starting Book RAG Application...");
		SpringApplication.run(BookRagApplication.class, args);
	}

	@PostConstruct
	public void init() {
		initService.init();
	}

	@Bean
	ToolCallbackProvider ragTools(VectorStoreService vectorStoreService) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(vectorStoreService)
				.build();
	}

}
