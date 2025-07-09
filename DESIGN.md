# AWS-SYS Design Documentation

## Composite Strategy Pattern Implementation

### Architecture Overview

The system implements a two-layer Composite Strategy Pattern for flexible request routing:

1. **Layer 1: API Gateway Routing**
   - **Strategy Interface**: `ApiGatewayIntegrationInterface`
     ```java
     public interface ApiGatewayIntegrationInterface {
         Response receiveFromGateway(Request request) throws Exception;
     }
     ```
   - **Concrete Strategies**: 
     - `EC2Integration`
     - `S3Integration`
     - `LoadBalancerIntegration`
   - **Context**: API Gateway

2. **Layer 2: Load Balancer Routing**
   - **Strategy Interface**: `TargetIntegrationInterface`
     ```java
     public interface TargetIntegrationInterface {
         Response receiveFromLoadBalancer(Request request) throws Exception;
     }
     ```
   - **Concrete Strategies**:
     - `EC2TargetGroup`
     - `LambdaTargetGroup`
   - **Context**: LoadBalancer

### Class Diagram

```mermaid
classDiagram
    class ApiGatewayIntegrationInterface {
        <<interface>>
        +receiveFromGateway(Request): Response
    }
    
    class TargetIntegrationInterface {
        <<interface>>
        +receiveFromLoadBalancer(Request): Response
    }
    
    class EC2Integration {
        +receiveFromGateway(Request): Response
    }
    
    class S3Integration {
        +receiveFromGateway(Request): Response
    }
    
    class LoadBalancerIntegration {
        +receiveFromGateway(Request): Response
        -targetGroups: List~TargetIntegrationInterface~
    }
    
    class EC2TargetGroup {
        +receiveFromLoadBalancer(Request): Response
    }
    
    class LambdaTargetGroup {
        +receiveFromLoadBalancer(Request): Response
    }
    
    ApiGatewayIntegrationInterface <|-- EC2Integration
    ApiGatewayIntegrationInterface <|-- S3Integration
    ApiGatewayIntegrationInterface <|-- LoadBalancerIntegration
    TargetIntegrationInterface <|-- EC2TargetGroup
    TargetIntegrationInterface <|-- LambdaTargetGroup
    
    LoadBalancerIntegration "1" *-- "*" TargetIntegrationInterface : contains
    
    class ApiGateway {
        -strategy: ApiGatewayIntegrationInterface
        +handleRequest(Request): Response
    }
    
    class LoadBalancer {
        -strategy: TargetIntegrationInterface
        +handleRequest(Request): Response
    }
    
    ApiGateway o-- ApiGatewayIntegrationInterface : uses
    LoadBalancer o-- TargetIntegrationInterface : uses
```

### Sequence Diagram

```mermaid
sequenceDiagram
    participant Client
    participant ApiGateway
    participant LoadBalancer
    participant TargetGroup
    participant Target
    
    Client->>ApiGateway: Request
    
    alt EC2/S3 Request
        ApiGateway->>EC2/S3: receiveFromGateway(request)
        EC2/S3-->>ApiGateway: Response
    else LoadBalancer Request
        ApiGateway->>LoadBalancer: receiveFromGateway(request)
        LoadBalancer->>TargetGroup: receiveFromLoadBalancer(request)
        TargetGroup->>Target: Forward Request
        Target-->>TargetGroup: Response
        TargetGroup-->>LoadBalancer: Response
        LoadBalancer-->>ApiGateway: Response
    end
    
    ApiGateway-->>Client: Response
```

### Key Characteristics

1. **Layered Strategy**
   - Each layer handles a specific routing concern
   - Strategies can be composed (LoadBalancer contains TargetGroups)

2. **Extensibility**
   - Easy to add new integration types at either layer
   - New target types can be added without modifying existing code

3. **Maintainability**
   - Clear separation of concerns
   - Each component has a single responsibility

### Adding New Components

#### Adding a New API Gateway Integration
1. Implement `ApiGatewayIntegrationInterface`
2. Add routing logic in API Gateway

#### Adding a New Target Group Type
1. Implement `TargetIntegrationInterface`
2. Register with LoadBalancer

### Benefits of This Design

1. **Flexibility**: Easy to modify routing logic at any level
2. **Scalability**: New routing strategies can be added without affecting existing ones
3. **Testability**: Each strategy can be tested in isolation
4. **Maintainability**: Clear separation of concerns between routing layers
