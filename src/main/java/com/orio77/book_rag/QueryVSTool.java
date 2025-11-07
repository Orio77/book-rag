package com.orio77.book_rag;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QueryVSTool {

    private final static Logger logger = LoggerFactory.getLogger(QueryVSTool.class);

    public List<String> query(String query, int topK) {
        // Query pinecone vector database with user question vector to get top-k similar
        // books
        logger.info("Querying vector database for top {} similar books to query: {}", topK, query);
        return new ArrayList<>();
    }
}
