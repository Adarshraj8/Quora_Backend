# Quora Backend Clone

A backend clone of Quora built with **Spring Boot**, supporting user authentication (local & Google OAuth2), JWT-based authorization, and core features like question posting, answering, and user profiles.

---

## 🚀 Features

- 🔐 User Authentication  
  - Sign up / Sign in with username & password
  - Google OAuth2 Login
  - JWT Access & Refresh Tokens

- 📄 Question & Answer System  
  - Post questions
  - Submit answers
  - View user-specific content

- 👤 User Management  
  - User profile
  - View by username or email

- 🔒 Secure API  
  - Role-based protected routes
  - JWT token validation

---

## 🛠 Tech Stack

- **Backend:** Java, Spring Boot, Spring Security
- **Authentication:** JWT, OAuth2 (Google)
- **Database:** MySQL or H2 (as per config)
- **Build Tool:** Maven
- **Other:** Lombok, Postman (for testing)

---

## 📦 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL (or H2 for local testing)

### Clone the Repo

```bash
git clone https://github.com/Adarshraj8/Quora_Backend.git
cd Quora_Backend


spring.datasource.url=jdbc:mysql://localhost:3306/quora_db
spring.datasource.username=root
spring.datasource.password=your_password

Run the App
bash
Copy
Edit
mvn spring-boot:run

🔑 API Endpoints
Auth Routes
Method	Endpoint	Description
POST	/api/auth/signup	Register new user
POST	/api/auth/signin	Login with username/password
POST	/api/auth/google	Google login with ID token
POST	/api/auth/refresh	Refresh access token


Testing
Use Postman to test login, token, and protected APIs.

Validate JWT access by hitting secured endpoints with the Authorization header.


🙌 Contributions
Pull requests are welcome! For major changes, open an issue first to discuss what you’d like to change.

✨ Author
Adarsh Raj – GitHub
