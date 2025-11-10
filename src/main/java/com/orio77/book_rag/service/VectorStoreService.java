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

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VectorStoreService {

    private static final int batchSize = 50;

    @Autowired
    private VectorStore vectorStore;

    public void upsertDocuments(List<Document> docs) {
        log.info("Upserting {} documents", docs.size());

        // Split documents into batches
        List<List<Document>> batches = Lists.partition(docs, batchSize);
        log.info("Split documents into {} batches", batches.size());

        // Upsert each batch into the vector store
        batches.stream().forEach(batch -> {
            vectorStore.add(batch);
            log.info("Upserted batch of {} documents", batch.size());
        });

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

            log.info("Found {} documents to delete", allDocs.size());

            // Delete documents by their IDs
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
            // Perform a test search to check if any documents exist
            List<Document> test = vectorStore.similaritySearch(
                    SearchRequest.builder().query("test").topK(1).build());
            return !test.isEmpty();
        } catch (Exception e) {
            log.warn("Error checking if vector store has documents", e);
            return false;
        }
    }

}
