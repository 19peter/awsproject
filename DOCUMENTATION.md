# AWS-SYS-MOCK Documentation

## 1. Project Overview
AWS-SYS-MOCK is a Java-based simulation of core AWS services including EC2, S3, API Gateway, and Load Balancers. The project provides a simplified but functional representation of how these services interact in a cloud environment.

## 2. Architecture

The system follows a modular architecture with clear separation of concerns:

- **Core Layer**: Base classes and interfaces
- **Components**: Implementation of AWS services
- **Interfaces**: Contracts between components
- **Builders**: For creating complex objects
- **Scenarios**: Example use cases
- **DTOs**: Data transfer objects

## 3. Core Components

### 3.1 AWSObject
Base class for all AWS resources with common properties like ID, name, and running state.

### 3.2 Main AWS Services
- **EC2**: Virtual servers in the cloud
- **S3**: Object storage service
- **API Gateway**: API management
- **Load Balancer**: Distributes traffic
- **Auto Scaling**: Automatic scaling of resources

## 4. Design Patterns

### 4.1 Composite Strategy Pattern

#### Overview
The system implements a two-layer Composite Strategy Pattern for flexible request routing. This pattern combines the Strategy and Composite patterns to create a hierarchical structure of interchangeable algorithms.

#### Implementation

**Layer 1: API Gateway Routing**
- **Strategy Interface**: `ApiGatewayIntegrationInterface`
  ```java
  public interface ApiGatewayIntegrationInterface {
      Response receiveFromGateway(Request request) throws Exception;
  }
  ```
- **Concrete Strategies**:
  - `EC2Integration`: Handles direct EC2 requests
  - `S3Integration`: Manages S3 object operations
  - `LoadBalancerIntegration`: Routes to appropriate load balancer

**Layer 2: Load Balancer Routing**
- **Strategy Interface**: `TargetIntegrationInterface`
  ```java
  public interface TargetIntegrationInterface {
      Response receiveFromLoadBalancer(Request request) throws Exception;
  }
  ```
- **Concrete Strategies**:
  - `EC2TargetGroup`: Manages EC2 instances
  - `LambdaTargetGroup`: Handles Lambda function invocations

#### Key Benefits
1. **Flexible Routing**: Each layer can independently determine how to process requests
2. **Extensibility**: New strategies can be added without modifying existing code
3. **Maintainability**: Clear separation of concerns between routing layers
4. **Scalability**: Easy to add new routing logic at any level

### 4.2 Builder Pattern
Used in the `Builders` package to create complex objects step by step.

### 4.3 Template Method Pattern
- `TargetMonitor` defines the skeleton of monitoring operations
- `EC2TargetMonitor` provides concrete implementations of the monitoring steps
- This pattern allows defining the algorithm's structure while letting subclasses redefine certain steps

### 4.4 Observer Pattern
- `TargetStateObserverInterface` for monitoring state changes
- Used in load balancing and auto-scaling to notify components of state changes
- `EC2TargetMonitor` notifies observers when the EC2 instance's state changes

## 5. Key Classes

### 5.1 EC2 (`Components/EC2/EC2.java`)
- Represents an EC2 instance
- Implements `ApiGatewayIntegrationInterface` and `LifecycleManager`
- Uses `EC2TargetMonitor` for monitoring instance state and notifying observers

### 5.2 LoadBalancer (`Components/LoadBalancer/LoadBalancer.java`)
- Distributes incoming traffic
- Manages target groups
- Implements `ApiGatewayIntegrationInterface` and `LifecycleManager`

### 5.3 TargetGroup (`Components/LoadBalancer/TargetGroup/`)
- Abstract base class for different types of target groups
- `EC2TargetGroup` manages EC2 instances
- `LambdaTargetGroup` manages Lambda functions

### 5.4 TargetMonitor (`Components/Monitors/TargetMonitor.java`)
- Abstract base class that defines the monitoring algorithm structure
- Uses the Template Method pattern to allow subclasses to implement specific monitoring behavior
- Manages observer registration and notification

### 5.5 EC2TargetMonitor (`Components/Monitors/EC2TargetMonitor.java`)
- Implements EC2-specific monitoring functionality
- Extends `TargetMonitor` to provide concrete implementations of monitoring steps
- Manages EC2 instance state and notifies observers of changes

### 5.6 API Gateway (`Components/ApiGateway/ApiGateway.java`)
- Manages APIs and routes
- Integrates with other services

## 6. Interfaces

### 6.1 LifecycleManager
- `initialize()`: Initialize the component
- `shutdown()`: Clean up resources
- `isRunning()`: Check if component is running

### 6.2 ApiGatewayIntegrationInterface
- `receiveFromGateway()`: Handle incoming API requests

### 6.3 TargetIntegrationInterface
- `receiveRequest()`: Handle incoming requests
- `addTarget()`: Add a target to the group
- `removeTarget()`: Remove a target from the group

### 6.4 TargetStateObserverInterface
- `onTargetStateChanged()`: Handle target state changes
- `onRunningRequestsChanged()`: Handle running requests changes

## 7. Data Flow

1. Request enters through API Gateway
2. API Gateway routes to appropriate service
3. For load-balanced services:
   - Request goes to Load Balancer
   - Load Balancer selects target from Target Group
   - Target processes request and returns response

## 8. Error Handling

- Uses Java exceptions for error conditions
- Logging with Log4j
- Graceful degradation when possible

## 9. Concurrency Model

- Thread-safe operations using `synchronized`
- `ExecutorService` for managing worker threads
- Thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`)

## 10. Future Enhancements

1. **Metrics and Monitoring**: Add detailed metrics collection
2. **More AWS Services**: Add support for more AWS services
3. **Configuration Management**: Externalize configuration
4. **Testing**: Add comprehensive test suite
5. **Documentation**: Expand with more examples and diagrams

## 11. Dependencies

- Java 8+
- Log4j 2.x
- JUnit (for testing)

## 12. Building and Running

```bash
# Build the project
mvn clean install

# Run the application
java -jar target/aws-sys-mock.jar
```

## 13. Example Scenarios

See the `Scenarios` package for example use cases demonstrating how to use the AWS services together.
