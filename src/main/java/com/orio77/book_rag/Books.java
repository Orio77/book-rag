package com.orio77.book_rag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class Books {

    @Value("${orio.book-rag.books-path}")
    private String booksPath;

    @Bean
    List<FileSystemResource> getBooks() {
        try {
            Path directory = Paths.get(booksPath);

            if (!Files.exists(directory)) {
                log.warn("Books directory does not exist: {}", booksPath);
                return List.of();
            }

            try (Stream<Path> paths = Files.walk(directory, 1)) {
                List<FileSystemResource> books = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                        .map(path -> {
                            log.info("Loading book: {}", path.getFileName());
                            return new FileSystemResource(path.toFile());
                        })
                        .toList();

                log.info("Found {} PDF books in directory: {}", books.size(), booksPath);
                return books;
            }
        } catch (IOException e) {
            log.error("Error reading books directory: {}", booksPath, e);
            return List.of();
        }
    }
}