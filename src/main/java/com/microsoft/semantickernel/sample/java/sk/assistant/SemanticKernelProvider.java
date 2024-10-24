package com.microsoft.semantickernel.sample.java.sk.assistant;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.http.policy.FixedDelayOptions;
import com.azure.core.http.policy.RetryOptions;
import com.azure.core.util.HttpClientOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.connectors.data.hsqldb.HSQLDBVectorStoreQueryProvider;
import com.microsoft.semantickernel.connectors.data.jdbc.JDBCVectorStore;
import com.microsoft.semantickernel.connectors.data.jdbc.JDBCVectorStoreOptions;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.contextvariables.converters.ContextVariableJacksonConverter;
import com.microsoft.semantickernel.data.vectorstorage.VectorStore;
import com.microsoft.semantickernel.implementation.EmbeddedResourceLoader;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.sample.java.sk.assistant.controllers.CustomerController;
import com.microsoft.semantickernel.sample.java.sk.assistant.datastore.CustomerDataStore;
import com.microsoft.semantickernel.sample.java.sk.assistant.models.Customer;
import com.microsoft.semantickernel.sample.java.sk.assistant.models.Customers;
import com.microsoft.semantickernel.sample.java.sk.assistant.models.Rules;
import com.microsoft.semantickernel.sample.java.sk.assistant.skills.CustomersPlugin;
import com.microsoft.semantickernel.sample.java.sk.assistant.skills.Emailer;
import com.microsoft.semantickernel.semanticfunctions.HandlebarsPromptTemplateFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionYaml;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hsqldb.jdbc.JDBCDataSourceFactory;
import reactor.core.publisher.Flux;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A factory class that constructs kernels for use by the application
 */
@ApplicationScoped
public class SemanticKernelProvider {
    private static DataSource datasource;
    private final CustomerController customerController;

    private final String model;
    private final Customers customers;
    private final String openAiEndpoint;
    private final CustomerDataStore customerDataStore;
    private final AuthenticationProvider authenticationProvider;
    private final String profile;
    private VectorStore vectorStore;
    private JDBCVectorStore memoryStore;

    @Inject
    public SemanticKernelProvider(
            Customers customers,
            Rules rules,

            @ConfigProperty(name = "openai.endpoint")
            String openAiEndpoint,

            AuthenticationProvider authenticationProvider,
            @ConfigProperty(name = "chatcompletion.model", defaultValue = "gpt-4o")
            String model,

            @ConfigProperty(name = "profile", defaultValue = "deploy")
            String profile
    ) {
        this.model = model;
        this.customers = customers;
        this.openAiEndpoint = openAiEndpoint;
        this.authenticationProvider = authenticationProvider;
        this.profile = profile;

        System.out.println("END: " + openAiEndpoint);

        this.customerDataStore = new CustomerDataStore(this);

        this.customerController = new CustomerController(this, customerDataStore, rules);

        // Add converter to global types
        ContextVariableTypes.addGlobalConverter(
                ContextVariableJacksonConverter
                        .builder(Customer.class,
                                new ObjectMapper()
                                        .registerModule(new JavaTimeModule())).build());
    }

    public void init() {
        Flux.fromIterable(customers.getCustomers())
                .concatMap(customerController::saveCustomerFacts)
                .blockLast();
    }

    private OpenAIAsyncClient getOpenAIAsyncClient() {

        RetryOptions retryOptions = new RetryOptions(new FixedDelayOptions(2, Duration.ofSeconds(10)));


        OpenAIClientBuilder builder = new OpenAIClientBuilder()
                .clientOptions(new HttpClientOptions()
                        .setReadTimeout(Duration.ofSeconds(10))
                        .setResponseTimeout(Duration.ofSeconds(10)))
                .retryOptions(retryOptions)
                .endpoint(openAiEndpoint);

        if ("devEnv".equals(profile)) {
            return builder
                    .credential(authenticationProvider.getKeyCredential())
                    .buildAsyncClient();
        } else {
            return builder
                    .credential(authenticationProvider.getCredentials())
                    .buildAsyncClient();
        }
    }

    public Kernel getKernel() {
        Kernel.Builder builder = Kernel.builder()
                .withAIService(ChatCompletionService.class,
                        OpenAIChatCompletion.builder()
                                .withModelId(model)
                                .withOpenAIAsyncClient(getOpenAIAsyncClient())
                                .build()
                );
        return addSkills(builder).build();
    }

    private Kernel.Builder addSkills(Kernel.Builder builder) {
        try {
            builder.withPlugin(
                    KernelPluginFactory.createFromFunctions(
                            "CustomerSkills",
                            "QueryInfo",
                            List.of(
                                    KernelFunctionYaml.fromPromptYaml(
                                            EmbeddedResourceLoader.readFile(
                                                    "skills/CustomerSkills/QueryInfo/queryInfo.yml",
                                                    SemanticKernelProvider.class,
                                                    EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT
                                            ),
                                            new HandlebarsPromptTemplateFactory())
                            )
                    )
            );

            builder.withPlugin(
                    KernelPluginFactory.createFromFunctions(
                            "CustomerSkills",
                            "GenerateDownloadLinks",
                            List.of(
                                    KernelFunctionYaml.fromPromptYaml(
                                            EmbeddedResourceLoader.readFile(
                                                    "skills/CustomerSkills/GenerateDownloadLinks/generateDownloadLinks.yml",
                                                    SemanticKernelProvider.class,
                                                    EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT
                                            ),
                                            new HandlebarsPromptTemplateFactory())
                            )
                    )
            );

            builder.withPlugin(
                    KernelPluginFactory.createFromObject(new CustomersPlugin(
                            customers,
                            this,
                            customerDataStore
                    ), "CustomerSkills")
            );

            builder.withPlugin(
                    KernelPluginFactory.createFromFunctions(
                            "Language",
                            "StatementType",
                            List.of(
                                    KernelFunctionYaml.fromPromptYaml(
                                            EmbeddedResourceLoader.readFile(
                                                    "skills/Language/StatementType/statementType.yml",
                                                    SemanticKernelProvider.class,
                                                    EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT
                                            ),
                                            new HandlebarsPromptTemplateFactory())
                            )
                    )
            );
            builder.withPlugin(KernelPluginFactory.createFromObject(customerController, "CustomerController"));
            builder.withPlugin(KernelPluginFactory.createFromObject(new Emailer(), "Emailer"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder;
    }

    public synchronized VectorStore getLocalMemoryStore() {
        //return new VolatileMemoryStore();

        if (vectorStore == null) {
            try {

                DataSource datasource = getDataSource();
                vectorStore = JDBCVectorStore.builder()
                        .withDataSource(datasource)
                        .withOptions(
                                JDBCVectorStoreOptions.builder()
                                        .withQueryProvider(HSQLDBVectorStoreQueryProvider.builder()
                                                .withDataSource(datasource)
                                                .build()
                                        )
                                        .build()
                        )
                        .build();

                return vectorStore;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return vectorStore;
    }

    private synchronized static DataSource getDataSource() throws Exception {
        if (datasource != null) {
            return datasource;
        }

        Properties properties = new Properties();
        properties.putAll(
                Map.of(
                        "url", "jdbc:hsqldb:file:/tmp/testdb;sql.syntax_mys=true",
                        "user", "SA",
                        "password", ""
                )
        );

        datasource = JDBCDataSourceFactory.createDataSource(properties);
        return datasource;
    }

    public CustomerController getCustomerController() {
        return customerController;
    }

    public synchronized VectorStore getMemoryStore() {
        if (memoryStore != null) {
            return memoryStore;
        }

        try {
            HSQLDBVectorStoreQueryProvider queryProvider = HSQLDBVectorStoreQueryProvider.builder()
                    .withDataSource(getDataSource())
                    .setDefaultVarCharLength(OpenAITextEmbeddingGenerationService.EMBEDDING_DIMENSIONS_LARGE * 50)
                    .build();

            memoryStore = JDBCVectorStore.builder()
                    .withDataSource(getDataSource())
                    .withOptions(JDBCVectorStoreOptions.builder()
                            .withQueryProvider(queryProvider)
                            .build())
                    .build();

            return memoryStore;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OpenAITextEmbeddingGenerationService getEmbedding() {
        return OpenAITextEmbeddingGenerationService.builder()
                .withOpenAIAsyncClient(getOpenAIAsyncClient())
                .withModelId("text-embedding-3-large")
                .withDimensions(1536)
                .build();
    }
}
