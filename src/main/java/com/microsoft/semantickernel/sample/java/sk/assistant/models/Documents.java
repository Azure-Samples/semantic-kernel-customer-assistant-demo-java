package com.microsoft.semantickernel.sample.java.sk.assistant.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Documents(@JsonProperty("clientId") String clientId,
                        @JsonProperty("documents") List<Document> documents) {
}