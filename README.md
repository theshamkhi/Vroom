# 🚗 Vroom - Interactive Driving Education Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

> **Transform driver education with interactive, video-based learning scenarios**

Vroom is a comprehensive platform that revolutionizes driving education through interactive video scenarios, real-time progress tracking, and gamified learning. Built with Spring Boot and modern web technologies.

---

## 🎯 Key Features

### 🎬 **Interactive Video Scenarios**
- **Timestamp-based interaction points** - Pause videos at critical moments for questions
- **16 driving themes** - Urban, highway, parking, weather conditions, and more
- **3 difficulty levels** - Beginner, intermediate, and advanced scenarios
- **Real-time answer validation** - Instant feedback on student responses

### 📊 **Comprehensive Progress Tracking**
- **Individual student analytics** - Track scores, time spent, and completion rates
- **Attempt history** - Monitor improvement across multiple attempts
- **Detailed statistics** - Success rates per question, scenario analytics
- **Performance insights** - Identify strengths and areas for improvement

### 🏆 **Gamification & Engagement**
- **Achievement badges** - Reward milestones and skill mastery
- **Automatic email notifications** - Beautiful HTML emails for badges and assignments
- **Leaderboards ready** - Track top performers (extendable)
- **Points system** - Earn points for correct answers

### 👥 **Multi-Role Support**
- **Students** - Learn through interactive scenarios, track progress
- **Instructors** - Create content, assign scenarios, monitor students
- **Admins** - Manage platform, users, and content

### 📹 **Flexible Media Management**
- **Dual storage support** - Local file system or AWS S3
- **Automatic thumbnails** - Generated on upload
- **Video streaming** - Efficient HTTP streaming
- **Profile-based switching** - Toggle between local and cloud with one line

---

## 🏗️ Architecture

### Multi-Module Spring Boot Design

```
vroom-platform/
├── 🔐 security/        # Authentication & user management
├── 📧 notification/    # Email service with HTML templates
├── 📚 content/         # Scenarios, questions, badges
├── 🎥 media/           # Video upload & streaming
├── 📈 learning/        # Progress tracking & analytics
├── 🔧 shared/          # Common utilities & exceptions
└── 🚀 application/     # Main application & configuration
```

### Technology Stack

**Backend**
- **Spring Boot 3.2.0** - Modern Java framework
- **Spring Security** - JWT-based authentication
- **Spring Data JPA** - Database abstraction
- **PostgreSQL** - Primary database
- **Caffeine** - In-memory caching

**Communication**
- **RESTful APIs** - 60+ endpoints
- **JWT Tokens** - Secure authentication
- **CORS** - Frontend integration ready

**Email & Notifications**
- **JavaMail** - SMTP email sending
- **Thymeleaf** - HTML email templates
- **Async Processing** - Non-blocking operations

**Media Storage**
- **Local File System** - Default storage
- **AWS S3** - Cloud storage option
- **Presigned URLs** - Secure temporary access

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 15+**

### 1. Clone & Setup

```bash
git clone https://github.com/theshamkhi/Vroom.git
cd Vroom
```

### 2. Configure

Edit `application/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vroom_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3. Build & Run

```bash
mvn clean install -DskipTests
cd application
mvn spring-boot:run
```

### 4. Access

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html

---