package com.microsoft.semantickernel.sample.java.sk.assistant.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Document(@JsonProperty("documentId") String documentId, @JsonProperty("title") String title) {
}
