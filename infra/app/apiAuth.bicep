param name string
param location string = resourceGroup().location
param tags object = {}

param apiIdentityId string
param apiIdentityPrincipalId string

// Cogitive Services User Role
var roleDefinitionID = 'a97b65f3-24c7-4388-baec-2e87135dc908'

resource roleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
    name: guid(subscription().id, resourceGroup().id, apiIdentityId, roleDefinitionID)
    scope: resourceGroup()
    properties: {
      principalId: apiIdentityPrincipalId
      roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', roleDefinitionID)
      principalType: 'ServicePrincipal'
    }
}
