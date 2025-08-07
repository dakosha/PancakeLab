# Pancake Lab - Dojo Ordering System

A robust, object-oriented pancake ordering system for the Coding Dojo, designed to prevent the chaos caused by Dr. Fu Man Chu's malicious ingredient combinations.

## Problem Solved

The original system had several critical issues:
- **Invalid ingredient combinations**: Mustard with chocolate and milk was allowed
- **Missing validation**: No checks for building/room existence
- **Data race conditions**: Concurrent access issues
- **Exposed internal objects**: Domain objects leaked to clients
- **Hardcoded recipes**: Inflexible ingredient management

## Architecture

The system follows clean architecture principles with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│       API       │    │     Service     │    │   Repository    │
│   (Public)      │◄──►│   (Business)    │◄──►│   (Data)        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      DTOs       │    │     Domain      │    │   In-Memory     │
│   (Transfer)    │    │   (Entities)    │    │   (Storage)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Key Components

### Domain Layer
- **`Ingredient`**: Validates ingredient compatibility and prevents invalid combinations
- **`Pancake`**: Manages ingredient collection with validation
- **`Order`**: Handles order lifecycle and delivery validation

### Service Layer
- **`PancakeLabService`**: Orchestrates business operations with thread safety
- **`OrderRepository`**: Abstract data access interface

### API Layer
- **`PancakeLabAPI`**: Public interface with input validation
- **`OrderDTO`/`PancakeDTO`**: Data transfer objects (no domain exposure)
- API can be exposed via Web or any other transport layers.

### Repository Layer
- **`InMemoryOrderRepository`**: Thread-safe in-memory storage
- We might have any kind of Repository implementation here.

## Security & Validation Features

### Ingredient Validation
- **Compatibility checking**: Sweet and savory ingredients are incompatible
- **Malicious combination prevention**: Mustard + Chocolate combinations blocked
- **Type validation**: Ingredient types must be valid

### Order Validation
- **Building format**: Alphanumeric characters only
- **Room number format**: Numeric characters only
- **Pancake validation**: Must contain flour and egg (basic ingredients)

### Thread Safety
- **ReadWriteLock**: Concurrent access protection
- **Immutable objects**: Domain objects are immutable
- **Thread-safe repository**: ConcurrentHashMap for storage

## Usage Examples

### Disciple Operations
```java
// Create an order
String orderId = api.createOrder("BuildingA", "101");

// Add pancakes
String pancakeId = api.addPancakeToOrder(orderId);

// Add ingredients (one by one)
api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
api.addIngredientToPancake(orderId, pancakeId, "Sugar", "SUGAR");

// Complete the order
api.completeOrder(orderId);
```

### Chef Operations
```java
// Start preparing
api.startPreparingOrder(orderId);

// Mark ready for delivery
api.markOrderReadyForDelivery(orderId);
```

### Delivery Operations
```java
// Deliver the order
api.deliverOrder(orderId);
```

## Testing

The system follows Test-Driven Development (TDD) with comprehensive test coverage:

- **Domain Tests**: Ingredient, Pancake, and Order validation
- **Service Tests**: Business logic and thread safety
- **API Tests**: Input validation and DTO conversion
- **Integration Tests**: End-to-end workflows

## Order Lifecycle

```
CREATED → COMPLETED → PREPARING → READY_FOR_DELIVERY → DELIVERED
    ↓
CANCELLED
```

## Validation Rules

### Ingredient Compatibility
- **Sweet ingredients**: Sugar, Chocolate, Honey, Syrup, Cream
- **Savory ingredients**: Salt, Pepper, Mustard, Cheese
- **Incompatible**: Sweet + Savory combinations
- **Forbidden**: Mustard + Chocolate combinations

### Order Rules
- **Building**: Alphanumeric characters only
- **Room**: Numeric characters only
- **Pancake**: Must contain Flour + Egg
- **Status transitions**: Strict state machine enforcement

## Design Principles

- **Object-Oriented Programming**: Rich domain models with behavior
- **TDD**: Comprehensive test coverage
- **Pure Java**: No external dependencies
- **API Protection**: DTOs prevent domain exposure
- **No Hardcoded Recipes**: Flexible ingredient system
- **Input Validation**: Comprehensive validation at all layers
- **Thread Safety**: Concurrent access protection
- **Immutability**: Domain objects are immutable

## Success Metrics

- **Prevents invalid combinations**: Mustard + Chocolate blocked
- **Validates delivery info**: Building/room format enforced
- **Thread-safe operations**: Concurrent access handled
- **Clean API**: No internal objects exposed
- **Flexible ingredients**: No hardcoded recipes
- **Comprehensive testing**: TDD approach followed
