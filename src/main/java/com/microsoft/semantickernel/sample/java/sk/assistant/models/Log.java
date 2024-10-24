package com.microsoft.semantickernel.sample.java.sk.assistant.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.semantickernel.services.textembedding.TextEmbeddingGenerationService;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record Log(List<LogEvent> log) {

    @JsonCreator
    public Log(
            @JsonProperty("log")
            List<LogEvent> log) {
        this.log = new ArrayList<>(log);
    }

    public Log() {
        this(new ArrayList<>());
    }

    public Log addEvent(LogEvent logEvent) {
        log.add(logEvent);
        return this;
    }

    public static class LogBuilder {

        private final TextEmbeddingGenerationService embeddingGenerationService;

        private final List<Pair<Instant, String>> events = new ArrayList<>();

        public LogBuilder(
                TextEmbeddingGenerationService embeddingGenerationService
        ) {
            this.embeddingGenerationService = embeddingGenerationService;
        }


        public LogBuilder addEvent(Instant minus, String data) {
            events.add(Pair.of(minus, data));
            return this;
        }


        public Mono<Log> build() {
            List<String> strs = events
                    .stream()
                    .map(Pair::getRight)
                    .toList();

            return embeddingGenerationService
                    .generateEmbeddingsAsync(strs)
                    .map(embeddings -> {
                        List<LogEvent> logEvents = new ArrayList<>();
                        for (int i = 0; i < events.size(); i++) {
                            LogEvent logEvent = new LogEvent(events.get(i).getLeft(), events.get(i).getRight(), embeddings.get(i).getVector());
                            logEvents.add(logEvent);
                        }
                        return logEvents;
                    })
                    .map(Log::new);
        }
    }
}
