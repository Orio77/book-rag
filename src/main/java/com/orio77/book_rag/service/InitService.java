package com.orio77.book_rag.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InitService {

    @Autowired
    private List<FileSystemResource> books;

    @Autowired
    private VectorStoreService vectorStoreService;

    private static final int chunkSizeTokens = 1000;

    public void init() {
        AtomicInteger upsertedDocumentCount = new AtomicInteger(0);

        if (!vectorStoreService.hasDocuments()) {
            // Clear existing documents in the vector store
            log.info("Clearing existing documents in the vector store...");
            vectorStoreService.clear();

            TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(chunkSizeTokens).build();

            // Process each book
            books.forEach(book -> {
                PagePdfDocumentReader reader = new PagePdfDocumentReader(book);
                // Convert PDF to documents
                List<Document> documents = reader.get();
                log.info("Loaded {} documents from the {} book", documents.size(), book.getFilename());

                // Split documents into chunks
                List<Document> chunks = splitter.split(documents);

                log.info("Split documents into {} chunks", chunks.size());

                // Upsert chunks into the vector store
                vectorStoreService.upsertDocuments(chunks);

                log.info("Upserted {} documents into the vector store", chunks.size());
                upsertedDocumentCount.addAndGet(chunks.size());
            });
        }

        log.info("Book RAG Application initialized successfully with {} documents upserted.",
                upsertedDocumentCount.get());
    }
}
