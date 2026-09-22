# MeetSpace — Meeting Room Reservation System

> **Stack**: Spring Boot 3 · PostgreSQL · JWT · Angular 21  
> **Role**: SDE-1/SDE-2 portfolio project — full-stack, production-ready structure

---

## Project Structure

```
meeting/
├── src/                          # Spring Boot backend (Maven standard)
│   └── main/java/com/rishabh/meeting/
│       ├── config/               # SecurityConfig, CORS
│       ├── controller/           # REST controllers
│       ├── dto/                  # Request/Response DTOs
│       ├── entity/               # JPA entities
│       ├── exception/            # Global exception handler
│       ├── repository/           # Spring Data JPA repos
│       ├── security/             # JWT filter, UserDetails
│       └── service/              # Business logic
│
├── frontend/                     # Angular 21 frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/             # Guards, interceptors, services, models
│   │   │   ├── features/         # Auth, Dashboard, Rooms, Reservations, Admin
│   │   │   └── shared/           # Navbar
│   │   ├── assets/
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css            # Global design system (CSS variables)
│   ├── angular.json
│   ├── package.json
│   ├── proxy.conf.json           # Dev proxy → localhost:8080
│   └── tsconfig.json
│
├── pom.xml                       # Maven build
├── mvnw / mvnw.cmd               # Maven wrapper
├── MeetingRoomReservation.postman_collection.json
└── README.md
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3, Java 21 |
| Auth | Spring Security + JJWT 0.12 |
| ORM | Spring Data JPA + Hibernate |
| DB | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Frontend | Angular 21 (standalone components) |
| HTTP | Angular HttpClient (withFetch) |
| Styling | Vanilla CSS design system (dark mode, CSS variables) |
| Build | Maven (backend) + @angular/build (frontend) |

---

## Running the Project

### Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+
- PostgreSQL 15+

### Backend (Spring Boot)
```bash
# From project root
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Frontend (Angular 21)
```bash
cd frontend
npm install
npm run serve        # dev server only  → http://localhost:4200
npm start            # starts both backend + frontend concurrently
```

The Angular dev server proxies `/api` requests to `http://localhost:8080` via `proxy.conf.json`.

---

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register user |
| POST | `/api/auth/login` | Public | Login → JWT |
| GET | `/api/rooms` | User | List active rooms |
| GET | `/api/rooms/available` | User | Search by time + capacity |
| POST | `/api/reservations` | User | Book a room |
| GET | `/api/reservations/my` | User | My bookings |
| DELETE | `/api/reservations/{id}` | User | Cancel booking |
| GET | `/api/admin/reservations` | Admin | All bookings |
| POST | `/api/rooms` | Admin | Create room |
| PUT | `/api/rooms/{id}` | Admin | Update room |
| DELETE | `/api/rooms/{id}` | Admin | Deactivate room |

---

## Frontend Pages

| Route | Role | Description |
|---|---|---|
| `/login` | Public | JWT login with quick-fill |
| `/register` | Public | Account creation |
| `/dashboard` | User | Stats + upcoming bookings |
| `/rooms` | User | Browse all active rooms |
| `/rooms/search` | User | Search by date/time/capacity + inline booking |
| `/reservations` | User | My bookings with cancel |
| `/admin/rooms` | Admin | Create, edit, deactivate rooms |
| `/admin/reservations` | Admin | Full booking history table |

---
