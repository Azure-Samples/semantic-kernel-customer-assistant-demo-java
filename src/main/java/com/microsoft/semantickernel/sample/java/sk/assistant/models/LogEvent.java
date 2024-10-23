package com.microsoft.semantickernel.sample.java.sk.assistant.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.data.vectorstorage.attributes.VectorStoreRecordDataAttribute;
import com.microsoft.semantickernel.data.vectorstorage.attributes.VectorStoreRecordKeyAttribute;
import com.microsoft.semantickernel.data.vectorstorage.attributes.VectorStoreRecordVectorAttribute;
import com.microsoft.semantickernel.data.vectorstorage.definition.DistanceFunction;

import java.time.Instant;
import java.util.List;

import static com.microsoft.semantickernel.sample.java.sk.assistant.controllers.CustomerInfo.getId;

public record LogEvent(
        @VectorStoreRecordKeyAttribute(storageName = "id")
        String id,
        @VectorStoreRecordDataAttribute(storageName = "timestamp")
        Instant timestamp,
        @VectorStoreRecordDataAttribute(storageName = "info")
        String event,
        @JsonIgnore
        @VectorStoreRecordVectorAttribute(dimensions = OpenAITextEmbeddingGenerationService.EMBEDDING_DIMENSIONS_LARGE, distanceFunction = DistanceFunction.COSINE_DISTANCE)
        List<Float> embedding
) {
    public LogEvent(Instant timestamp, String event, List<Float> embedding) {
        this(getId(event), timestamp, event, embedding);
    }
}