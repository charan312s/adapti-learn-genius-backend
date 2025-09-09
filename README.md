# AdaptiLearn Backend

A Spring Boot backend application for the AdaptiLearn adaptive learning platform.

## 🚀 Features

- **User Authentication**: Sign up, sign in, and JWT token management
- **MongoDB Integration**: NoSQL database for flexible data storage
- **Spring Security**: Secure endpoints with JWT authentication
- **RESTful API**: Clean and well-structured REST endpoints
- **CORS Support**: Cross-origin resource sharing enabled
- **Input Validation**: Request validation using Bean Validation

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security**
- **Spring Data MongoDB**
- **JWT (JSON Web Tokens)**
- **Maven**
- **MongoDB**

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.4+
- Git

## 🔧 Installation & Setup

### 1. Clone the repository
```bash
git clone <repository-url>
cd adapti-learn-genius/backend
```

### 2. Install dependencies
```bash
mvn clean install
```

### 3. Configure MongoDB
Make sure MongoDB is running on your local machine:
```bash
# Start MongoDB service
mongod
```

### 4. Configure environment variables
Copy `env.backend` to `.env` and update the values:
```bash
cp env.backend .env
# Edit .env file with your configuration
```

### 5. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User login
- `POST /api/auth/validate` - Token validation
- `GET /api/auth/health` - Health check

### Request/Response Examples

#### Sign Up
```json
POST /api/auth/signup
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Sign In
```json
POST /api/auth/signin
{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

## 🗄️ Database Schema

### User Collection
```json
{
  "_id": "ObjectId",
  "username": "string (unique)",
  "email": "string (unique)",
  "password": "string (encrypted)",
  "firstName": "string",
  "lastName": "string",
  "learningStyle": "string",
  "roles": ["string"],
  "enabled": "boolean",
  "createdAt": "datetime",
  "lastLoginAt": "datetime"
}
```

## 🔐 Security

- **Password Encryption**: BCrypt password hashing
- **JWT Authentication**: Stateless authentication with tokens
- **CORS Configuration**: Configurable cross-origin settings
- **Input Validation**: Request validation and sanitization

## 🚀 Development

### Project Structure
```
src/main/java/com/adaptilearn/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── model/          # Entity models
├── repository/     # Data access layer
├── security/       # Security utilities
└── service/        # Business logic
```

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn clean package
```

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8080 |
| `MONGODB_HOST` | MongoDB host | localhost |
| `MONGODB_PORT` | MongoDB port | 27017 |
| `MONGODB_DATABASE` | Database name | adaptilearn |
| `JWT_SECRET` | JWT signing secret | - |
| `JWT_EXPIRATION` | Token expiration (ms) | 86400000 |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support, email support@adaptilearn.com or create an issue in the repository.

