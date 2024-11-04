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

#### Deploy To Azure Container Apps

To deploy the application you will need a deployment of OpenAI. This will need to have 2 models deployed to it:

- A chat completion model, by default this is named `gpt-4o` but this can be configured.
- An embedding model, by default this is named `text-embedding-3-large` but this can be configured.

You will be asked for the resource group and the name of the OpenAI instance during deployment.

```bash
## If needed
# azd auth login

azd up
```

You can now browse to the web service of the deployed asset that will be displayed in the output of the `azd up`. Will
look like: `https://ca-web-XXXX.azurecontainerapps.io/`

#### Run Locally

Before running the application:

1. In the `docker` folder, make a copy of `demo.properties_example` to `demo.properties` (in the same folder), and fill
   in the required keys.

To run the application locally as a container, have Docker Desktop or Podman installed, then run the following command:

```bash
cd docker
docker compose up --build --force-recreate
```

When started the app will be available at `http://localhost:3000/`.

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

