# Spring Boot TDD Demo (Beginner)

A tiny Spring Boot project to learn TDD. It implements a trivial calculator: an `add(a,b)` service and a REST endpoint `/api/calc/add?a=1&b=2`.

## Requirements
- Java 21 (already installed in this environment)
- No Maven/Gradle needed: the Maven Wrapper `./mvnw` is included

## Project layout
- `src/main/java/com/example/demo/CalculatorService.java`: business logic
- `src/main/java/com/example/demo/CalculatorController.java`: REST endpoint
- `src/test/java/com/example/demo/CalculatorServiceTest.java`: unit test (service)
- `src/test/java/com/example/demo/CalculatorControllerTest.java`: web layer test (MockMvc)

## Run tests
```
./mvnw test
```

## Run the application
```
./mvnw spring-boot:run
```
Then open `http://localhost:8080/api/calc/add?a=5&b=7` (should return `12`).

## TDD walkthrough (red → green → refactor)
1. RED: Write a failing test for `CalculatorService.add`.
2. GREEN: Implement the simplest `add` to pass the test.
3. RED: Write a MockMvc test for `/api/calc/add` expecting `12`.
4. GREEN: Implement `CalculatorController` to satisfy the test.
5. REFACTOR: Keep code clean, small methods, clear names.

## Notes
- Tests use JUnit 5 and MockMvc. No database, no complexity—just enough to learn TDD with Spring.

