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

