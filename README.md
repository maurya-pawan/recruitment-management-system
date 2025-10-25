# 🧑‍💼 Recruitment Management System – Backend (Spring Boot)

A complete **Recruitment Management System Backend** built using **Spring Boot** and **H2 Database**, designed to handle job openings, user registration, resume uploads, and resume parsing through a third-party API.  
This project demonstrates **JWT authentication**, **role-based access control**, **file upload**, **third-party API integration**, and **Swagger API documentation**.

---

## 📘 Swagger API Documentation
Explore and test APIs using Swagger UI:  
[![Swagger UI](https://img.shields.io/badge/Swagger-UI-green?logo=swagger&logoColor=white)](http://localhost:9090/swagger-ui/index.html)


## 🚀 Features

### 👤 User Management
- **User Signup** (`/signup`) — Register as Admin or Applicant.
- **User Login** (`/login`) — Authenticate and receive JWT.
- Role-based access control (Admin / Applicant).

### 📄 Resume Upload & Parsing
- Applicants can upload **PDF or DOCX** resumes.
- Integrated with **API Layer Resume Parser** to extract:
  - Education  
  - Experience  
  - Skills  
  - Phone  
  - Email  

### 💼 Job Management
- Admins can create and manage job openings.
- Applicants can view and apply for job openings.
- Admins can view all applicants and their parsed resume data.

---

## 🧠 Tech Stack

| Component | Technology |
|------------|-------------|
| **Framework** | Spring Boot (v3+) |
| **Language** | Java 21 |
| **Database** | H2 (In-memory) |
| **Security** | Spring Security + JWT |
| **ORM** | Spring Data JPA |
| **Documentation** | Swagger UI |
| **Resume Parser** | [API Layer Resume Parser](https://api.apilayer.com/resume_parser/upload) |
| **Build Tool** | Maven |
| **Version Control** | GitHub |

---

## 🧩 API Endpoints Overview

### 🔐 Authentication
| Method | Endpoint | Description | Access |
|--------|-----------|--------------|--------|
| POST | `/signup` | Register user | Public |
| POST | `/login` | Login & get JWT token | Public |

### 👨‍💼 Applicant APIs
| Method | Endpoint | Description | Access |
|--------|-----------|--------------|--------|
| POST | `/uploadResume` | Upload applicant resume (PDF/DOCX) | Applicant |
| GET | `/jobs` | View all job openings | Applicant |
| GET | `/jobs/apply?job_id={id}` | Apply for a job | Applicant |

### 🧑‍💻 Admin APIs
| Method | Endpoint | Description | Access |
|--------|-----------|--------------|--------|
| POST | `/admin/job` | Create a new job opening | Admin |
| GET | `/admin/job/{job_id}` | View job & applicants | Admin |
| GET | `/admin/applicants` | View all applicants | Admin |
| GET | `/admin/applicant/{applicant_id}` | View applicant details | Admin |

---

## 🧾 Third-Party Resume Parser API

**Endpoint:**  
