# Task Management API

A simple RESTful API for task management built with Java and Spring Boot. This project demonstrates clean backend architecture and test-driven development (TDD) practices, making it beginner-friendly while showcasing professional development standards.

## 🚀 Features

- **CRUD Operations**: Create, read, update, and delete tasks
- **Task Status Management**: Mark tasks as completed or incomplete
- **Search Functionality**: Search tasks by title or description
- **Task Statistics**: Get completion statistics and analytics
- **RESTful API Design**: Following REST principles with proper HTTP status codes
- **Test-Driven Development**: Comprehensive unit and integration tests
- **Clean Architecture**: Layered architecture with separation of concerns
- **In-Memory Database**: H2 database for easy setup and testing
- **Validation**: Input validation with meaningful error messages

## 🛠️ Technology Stack

- **Java 17**: Modern Java features and syntax
- **Spring Boot 3.2.0**: Framework for building production-ready applications
- **Spring Data JPA**: Data access layer with automatic query generation
- **H2 Database**: In-memory database for development and testing
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking framework for unit tests
- **AssertJ**: Fluent assertion library
- **Maven**: Dependency management and build tool

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🏃‍♂️ Quick Start

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd task-management-api
```

### 2. Build the Project

```bash
mvn clean compile
```

### 3. Run Tests

```bash
mvn test
```

### 4. Start the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### 5. Access H2 Console (Optional)

Visit `http://localhost:8080/h2-console` to access the H2 database console:
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/tasks
```

### Endpoints

#### 1. Get All Tasks
```http
GET /api/tasks
```
**Response**: Array of task objects

#### 2. Get Task by ID
```http
GET /api/tasks/{id}
```
**Response**: Task object or 404 if not found

#### 3. Create New Task
```http
POST /api/tasks
Content-Type: application/json

{
    "title": "Learn Spring Boot",
    "description": "Complete the Spring Boot tutorial",
    "completed": false
}
```
**Response**: Created task with generated ID (201 Created)

#### 4. Update Task
```http
PUT /api/tasks/{id}
Content-Type: application/json

{
    "title": "Updated title",
    "description": "Updated description",
    "completed": true
}
```
**Response**: Updated task object or 404 if not found

#### 5. Delete Task
```http
DELETE /api/tasks/{id}
```
**Response**: 204 No Content or 404 if not found

#### 6. Mark Task as Completed
```http
PATCH /api/tasks/{id}/complete
```
**Response**: Updated task object or 404 if not found

#### 7. Mark Task as Incomplete
```http
PATCH /api/tasks/{id}/incomplete
```
**Response**: Updated task object or 404 if not found

#### 8. Get Tasks by Status
```http
GET /api/tasks/completed/{status}
```
Where `{status}` is `true` or `false`

#### 9. Search Tasks
```http
GET /api/tasks/search?keyword=spring
```
**Response**: Array of tasks matching the keyword

#### 10. Get Task Statistics
```http
GET /api/tasks/statistics
```
**Response**: 
```json
{
    "totalTasks": 10,
    "completedTasks": 6,
    "incompleteTasks": 4,
    "completionPercentage": 60.0
}
```

#### 11. Get Completed Tasks (Convenience)
```http
GET /api/tasks/completed
```

#### 12. Get Incomplete Tasks (Convenience)
```http
GET /api/tasks/incomplete
```

### Task Object Structure

```json
{
    "id": 1,
    "title": "Learn Spring Boot",
    "description": "Complete the Spring Boot tutorial",
    "completed": false,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

## 🧪 Test-Driven Development (TDD)

This project follows TDD principles throughout:

### Test Structure
```
src/test/java/
├── model/
│   └── TaskTest.java              # Unit tests for Task entity
├── repository/
│   └── TaskRepositoryTest.java    # Integration tests for repository
├── service/
│   └── TaskServiceTest.java       # Unit tests for service layer
└── controller/
    └── TaskControllerTest.java    # Integration tests for REST API
```

### Test Types

1. **Unit Tests**: Test individual components in isolation using Mockito
2. **Integration Tests**: Test component interactions with real database
3. **Web Layer Tests**: Test REST endpoints using MockMvc

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TaskServiceTest

# Run tests with coverage
mvn test jacoco:report
```

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────┐
│   Controller Layer  │  ← REST endpoints, HTTP handling
├─────────────────────┤
│   Service Layer     │  ← Business logic, transactions
├─────────────────────┤
│   Repository Layer  │  ← Data access, queries
├─────────────────────┤
│   Model Layer       │  ← Entities, domain objects
└─────────────────────┘
```

### Package Structure

```
com.example.taskmanagement/
├── TaskManagementApplication.java  # Main application class
├── controller/
│   └── TaskController.java         # REST endpoints
├── service/
│   └── TaskService.java            # Business logic
├── repository/
│   └── TaskRepository.java         # Data access
└── model/
    └── Task.java                   # Entity class
```

## 🔧 Configuration

### Application Properties
- **Database**: H2 in-memory database
- **Port**: 8080
- **Profile**: Development settings
- **Logging**: Debug level for application packages

### Key Features
- **Automatic table creation**: Using JPA DDL auto-generation
- **CORS enabled**: For frontend integration
- **Validation**: Bean validation with custom messages
- **Exception handling**: Global exception handlers

## 📝 Development Guidelines

### Adding New Features

1. **Write Tests First**: Follow TDD by writing tests before implementation
2. **Layer by Layer**: Implement from bottom up (Entity → Repository → Service → Controller)
3. **Validation**: Add appropriate validation annotations
4. **Documentation**: Update this README and add JavaDoc comments

### Code Style

- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Follow Spring Boot conventions
- Keep methods small and focused
- Use proper exception handling

## 🚀 Deployment

### Building for Production

```bash
# Create executable JAR
mvn clean package

# Run the JAR
java -jar target/task-management-api-1.0.0.jar
```

### Docker Support (Optional)

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/task-management-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Implement the feature
5. Ensure all tests pass (`mvn test`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

## 📚 Learning Resources

### Spring Boot
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### Testing
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)

### TDD
- [Test-Driven Development by Example](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Growing Object-Oriented Software, Guided by Tests](https://www.amazon.com/Growing-Object-Oriented-Software-Guided-Tests/dp/0321503627)

## 🐛 Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   - Change the port in `application.yml`: `server.port: 8081`

2. **Tests failing**
   - Ensure Java 17 is being used
   - Run `mvn clean test` to rebuild and test

3. **Database connection issues**
   - H2 is in-memory, no setup required
   - Check H2 console at `/h2-console`

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

Created as a demonstration of Spring Boot and TDD best practices for beginners.

---

**Happy Coding! 🎉**

