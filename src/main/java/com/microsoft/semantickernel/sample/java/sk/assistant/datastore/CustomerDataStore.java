package com.microsoft.semantickernel.sample.java.sk.assistant.datastore;

import com.microsoft.semantickernel.connectors.data.jdbc.JDBCVectorStoreRecordCollectionOptions;
import com.microsoft.semantickernel.data.vectorsearch.VectorSearchResult;
import com.microsoft.semantickernel.data.vectorstorage.VectorStore;
import com.microsoft.semantickernel.data.vectorstorage.VectorStoreRecordCollection;
import com.microsoft.semantickernel.data.vectorstorage.options.VectorSearchOptions;
import com.microsoft.semantickernel.implementation.EmbeddedResourceLoader;
import com.microsoft.semantickernel.sample.java.sk.assistant.SemanticKernelProvider;
import com.microsoft.semantickernel.sample.java.sk.assistant.controllers.CustomerInfo;
import com.microsoft.semantickernel.sample.java.sk.assistant.models.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CustomerDataStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDataStore.class);

    private final SemanticKernelProvider semanticKernelProvider;

    public CustomerDataStore(SemanticKernelProvider semanticKernelProvider) {
        this.semanticKernelProvider = semanticKernelProvider;
    }

    public Mono<VectorStoreRecordCollection<String, CustomerInfo>> getCollectionForCustomer(Customer customer) {
        VectorStoreRecordCollection<String, CustomerInfo> collection = getVectorStore()
                .getCollection(
                        getCollectionName(customer),
                        JDBCVectorStoreRecordCollectionOptions.<CustomerInfo>builder()
                                .withRecordClass(CustomerInfo.class)
                                .build());

        return collection
                .createCollectionIfNotExistsAsync()
                .doOnError(e -> {
                    LOGGER.error("Failed to create collection", e);
                });
    }


    public static Flux<String> loadCustomerDataFromResources(Customer customer) {
        return Flux.range(1, 100)
                .map(i -> {
                    return "data/" + customer.getUid() + "/" + i + ".txt";
                }).mapNotNull(file -> {
                    try {
                        return EmbeddedResourceLoader.readFile(file, Customer.class, EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                });
    }


    public Mono<List<CustomerInfo>> queryCustomer(Customer customer, String query) {
        // TODO call customerDataStore.queryCustomer when we have a real implementation

        return semanticKernelProvider.getEmbedding()
                .generateEmbeddingAsync(query)
                .flatMap(embeddings -> {
                    return getCollectionForCustomer(customer)
                            .flatMap(collection -> {
                                return collection.searchAsync(
                                        embeddings.getVector(),
                                        VectorSearchOptions.builder()
                                                .withIncludeVectors(true)
                                                .withLimit(30)
                                                .build()
                                );
                            });
                })
                .flatMapMany(it -> {
                    return Flux.fromIterable(it)
                            .map(VectorSearchResult::getRecord);
                })
                .collectList();
    }

    private VectorStore getVectorStore() {
        return semanticKernelProvider.getMemoryStore();
    }

    public static String getCollectionName(Customer customer) {
        return ("customer-" + customer.getUid()).replaceAll("-", "_");
    }


    public static String formId(String data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();

            for (byte i : hash) {
                hex.append(String.format("%02X", i));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

}
