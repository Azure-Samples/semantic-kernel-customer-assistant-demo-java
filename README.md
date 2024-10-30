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
cd infra/aca

## If needed
# azd auth login

azd up
```

When deployed, you will need to grant "Cogntive Services User" role to the managed identity of the api service. Inside
the resource group of the deployed resources, find the managed identity of the api service, named `id-api-xxxx`.
We need to [assign the "Cognitive Services User" role to the
`id-api-xxxx` managed identity](https://learn.microsoft.com/en-us/entra/identity/managed-identities-azure-resources/how-to-assign-access-azure-resource?pivots=identity-mi-access-portal).
Browse to your Azure OpenAI instance in the portal, select "Access control (IAM)", "Role assignments" and add a role
assignment for the managed identity.
After permission has been granted you can restart your application by re-running `azd up`.

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

## Troubleshooting

#### My API instance is crashing with a "DeploymentNotFound" error

You have probably not deployed the chat completion and embeddings OpenAI models to the instance, or then names of your
deployments do not match the configured values in demo.properties (default values are `gpt-4o` and `text-embedding-3-large`).

#### I receive errors when running the application "json_schema is not supported"

Ensure that the model you have deployed supports the `json_schema` format, at time of writing this requires:
- gpt-4o-mini-2024-07-18 and later
- gpt-4o-2024-08-06 and later
See: https://platform.openai.com/docs/guides/structured-outputs/introduction

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

