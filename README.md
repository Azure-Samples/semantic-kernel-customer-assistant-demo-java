# Customer Assistant

This application is a customer assistant that can answer questions and also take actions about and towards a customer's
account.

## Features

This application utilizes the following features:

- Semantic Kernel for Java
    - Vector data stores
    - Chat completions with OpenAI
    - Embedding with OpenAI
- OpenAI
- React UI
- Quarkus

## Getting Started

Before running the application in any way, proceed with the following:

1. In the `docker` folder, make a copy of `demo.properties_example` to `demo.properties` (in the same folder), and fill
   in the required keys.

To run the application locally as a container, have Docker Desktop or Podman installed, then run the following command:

```bash
cd docker
docker compose up --build --force-recreate
```

#### Debug

To run the application in debugging mode, from the customer-assistant folder, run:

Create a .env file at `customer-assistant/.env` containing:

```shell
OPENAI_ENDPOINT=https://<your-endpoint>.openai.azure.com
AZURE_CLIENT_SECRET=<openai-secret>
CHATCOMPLETION_MODEL=gpt-4o
EMBEDDING_MODEL=text-embedding-3-large
```

Deploy a local UI in a container:

```bash
docker-compose -f docker/debug/docker-compose-dev.yml up
```

## Resources

- [Semantic Kernel for Java](https://github.com/microsoft/semantic-kernel-java)
- [Semantic Kernel for Java Documentation](https://learn.microsoft.com/en-us/semantic-kernel/)

## Guidance

### Costs

This application uses Azure OpenAI, which may incur costs. Please refer to
the [Azure OpenAI website](https://azure.microsoft.com/en-us/pricing/details/cognitive-services/openai-service/) for
more information.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Important Security Notice

