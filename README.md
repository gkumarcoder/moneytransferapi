

# moneytransferapi

A service for money transfers between accounts

## Prerequisites

- Java 8
- Gradle
- Hibernate 5.0


## Overview

Simple java RESTful API for performing money transfer between two internal accounts.

## Run

```
./gradlew run
```
Generate sample data on application startup
```
./gradlew run -PgenerateData
```
The application starts on following host:port - [http://localhost:4567]

## Endpoints
  
  
| Method               | Type  | URL                                         | Response Body                                         |  
| :---:                | :---: | :---:                                       | :---:                                                 |
| Get all Client       | GET   | /api/clients                                | Returns all existing clients                          |
| Get specified Client | GET   | /api/clients/{id}                           | Returns the specified client                          |
| Account              | GET   | /api/clients/{clientId}/accounts            | Returns all accounts of the specified client          |
| Get specified account| GET   | /api/clients/{clientId}/accounts/{accountId}| Returns the specified account of the specified client |


POST http://localhost:4567/api/transfer - performs money transfer between two internal accounts
example request body:
  {
  	"fromAccount": 296896261,
  	"toAccount": 296896262,
  	"amount": 50.00
  }

## API tests

feature: Clients / Accounts REST API

  Scenario: Retrieving all clients
    Given system contains following clients
    
      | firstName     | lastName   |
      | Bala          | Kumar      |
      | Ankit         | Jha        |
    When client requests GET /clients
    Then response status is 200
    And response contains 2 clients
    And response includes the following clients
    
      | order  | firstName     | lastName   |
      | 0      | Bala          | Kumar      |
      | 1      | Ankit         | Jha        |

  Scenario: Retrieving single client
    Given system contains following clients
    
      | firstName     | lastName   |
      | Bala          | Kumar      |
      | Ankit         | Jha        |
      
    When client requests GET /clients/<pankajId>
    Then response status is 200
    And response includes the following client
      | firstName | Bala     |
      | lastName  | Kumar    |
