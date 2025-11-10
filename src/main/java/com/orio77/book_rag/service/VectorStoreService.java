package com.orio77.book_rag.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VectorStoreService {

    @Autowired
    private VectorStore vectorStore;

    public void upsertDocuments(List<Document> docs) {
        log.info("Upserting {} documents", docs.size());

        // Batch size to stay under Pinecone's 4MB limit
        int batchSize = 50; // Conservative batch size to avoid hitting the limit

        for (int i = 0; i < docs.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, docs.size());
            List<Document> batch = docs.subList(i, endIndex);

            log.info("Upserting batch {} of {} documents (documents {}-{})",
                    (i / batchSize) + 1,
                    (docs.size() + batchSize - 1) / batchSize,
                    i + 1,
                    endIndex);

            try {
                vectorStore.add(batch);
                log.info("Successfully upserted batch of {} documents", batch.size());
            } catch (Exception e) {
                log.error("Failed to upsert batch {}-{}: {}", i + 1, endIndex, e.getMessage());
                throw e;
            }
        }

        log.info("Successfully upserted all {} documents in batches", docs.size());
    }

    public void deleteDocuments(List<String> documentIds) {
        log.info("Deleting {} documents by IDs", documentIds.size());
        vectorStore.delete(documentIds);
        log.info("Deleted {} documents", documentIds.size());
    }

    public void deleteDocumentsByFilter(Expression filterExpression) {
        log.info("Deleting documents by filter: {}", filterExpression);
        vectorStore.delete(filterExpression);
        log.info("Documents deleted by filter");
    }

    public void clear() {
        log.info("Deleting all documents from the vector store...");
        try {
            // Try to get all documents first to get their IDs
            List<Document> allDocs = vectorStore.similaritySearch(
                    SearchRequest.builder().query("").topK(10000).build());

            if (!allDocs.isEmpty()) {
                List<String> docIds = allDocs.stream()
                        .map(Document::getId)
                        .toList();
                vectorStore.delete(docIds);
                log.info("Deleted {} documents from the vector store", docIds.size());
            } else {
                log.info("No documents found to delete");
            }
        } catch (Exception e) {
            log.warn("Could not delete documents using ID-based approach: {}", e.getMessage());
            log.info("Vector store may already be empty or doesn't support bulk deletion");
        }
    }

    @Tool(name = "query_vector_store", description = "Query the vector store for pages from philosophical and theological books.")
    public List<Document> query(
            @ToolParam(description = "The query string similarity search will be performed on", required = true) String query,
            @ToolParam(description = "The number of top results to return", required = true) int topK) {
        log.info("Searching for similar documents with query: '{}', topK: {}", query, topK);
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build());
        log.info("Found {} similar documents", results.size());
        return results;
    }

    public List<Document> query(String query, int topK, double similarityThreshold) {
        log.info("Searching for similar documents with query: '{}', topK: {}, threshold: {}",
                query, topK, similarityThreshold);
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).similarityThreshold(similarityThreshold).build());
        log.info("Found {} similar documents above threshold", results.size());
        return results;
    }

    public List<Document> query(String query, Expression filterExpression) {
        log.info("Searching with filter - query: '{}', filter: {}",
                query, filterExpression);
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).filterExpression(filterExpression).build());
        log.info("Found {} filtered documents", results.size());
        return results;
    }

    public boolean hasDocuments() {
        try {
            List<Document> test = vectorStore.similaritySearch(
                    SearchRequest.builder().query("test").topK(1).build());
            return !test.isEmpty();
        } catch (Exception e) {
            log.warn("Error checking if vector store has documents", e);
            return false;
        }
    }

}
