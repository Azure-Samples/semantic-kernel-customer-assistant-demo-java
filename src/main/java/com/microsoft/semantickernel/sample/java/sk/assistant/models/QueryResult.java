package com.microsoft.semantickernel.sample.java.sk.assistant.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QueryResult {
    private String result;
    private final Documents documents;

    @JsonCreator
    public QueryResult(
            @JsonProperty("result") String result, @JsonProperty("documents") Documents documents) {
        this.result = result;
        this.documents = documents;
    }

    public QueryResult(String result, String clientId) {
        this.result = result;
        this.documents = new Documents(clientId, List.of());
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Documents getDocuments() {
        return documents;
    }
}
