# PaperTrail - Government Document Deadline Tracker

Full end-to-end implementation:
- Java backend (Spring Boot + MySQL + JWT)
- Expo React Native frontend (Android)
- Registration/Login/Profile + Government document tracking + alerts
- OTP delivery using Twilio SMS and SMTP email

## Project Structure

- `backend` - Spring Boot API
- `frontend` - Expo React Native app
- `infra/mysql/init.sql` - MySQL initialization script
- `docker-compose.yml` - local MySQL container

## Features Implemented

- User registration with all required fields:
  - Name
  - Mobile
  - Email
  - Password
  - Address
  - Age (must be >= 18)
- Login with email or mobile + password
- JWT-based authentication
- Profile API and profile screen showing all registration details
- OTP endpoints:
  - Send OTP via SMS (Twilio) or Email (SMTP)
  - Verify OTP
- Government document module:
  - Add, update, delete, list documents
  - Document types enum with default renewal cycle
  - Priority sorting by nearest expiry
  - Alert classification: EXPIRED, CRITICAL (<=30), WARNING (<=90), SAFE
  - Alert summary endpoint

## Backend Setup (Java + MySQL)

## 1) Start MySQL

Option A: Docker

```bash
docker compose up -d
```

Option B: Local MySQL
Create database `papertrail_db` and run `infra/mysql/init.sql`.

## 2) Configure environment variables

Copy from `backend/.env.example` and set values in your shell.

Required minimum:
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`

For OTP:
- Twilio SMS: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER`
- Email OTP: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`

## 3) Run backend

```bash
cd backend
mvn spring-boot:run
```

API base URL: `http://localhost:8080/api`

## Frontend Setup (Expo React Native Android)

## 1) Install dependencies

```bash
cd frontend
npm install
```

## 2) API URL

Default Android emulator API URL is already configured:
- `http://10.0.2.2:8080/api`

If using physical device, change it to your machine LAN IP in:
- `frontend/app.json` (`expo.extra.apiBaseUrl`)

## 3) Run app

```bash
cd frontend
npm run android
```

## Key API Endpoints

Auth:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/profile`
- `POST /api/auth/otp/send`
- `POST /api/auth/otp/verify`

Documents:
- `POST /api/documents`
- `PUT /api/documents/{id}`
- `GET /api/documents`
- `GET /api/documents/{id}`
- `DELETE /api/documents/{id}`
- `GET /api/documents/alerts/summary`

## Complexity (Your requested analysis)

Let `n` be number of documents for a user.

- Add document: `O(1)` DB insert, plus validation constant cost.
- List documents sorted by expiry:
  - Current implementation loads from DB, then sorts in memory by expiry: `O(n log n)`.
- Alert summary scan: `O(n)`.
- Delete/update by id: average `O(1)` lookup by PK with DB index.
- Login/register: average `O(1)` with indexed email/mobile checks.
- Space complexity in app layer: `O(n)` when listing/sorting documents.

## Java Packages Used

Backend:
- `java.time` (`LocalDate`, `LocalDateTime`, `ChronoUnit`)
- `java.util` (`List`, `Comparator`, `PriorityQueue`, `Optional`, `Random`)

Framework/library dependencies (backend):
- Spring Boot Web, Validation, Security, Data JPA, Mail
- MySQL Connector/J
- JJWT
- Twilio Java SDK

Frontend:
- Expo + React Native
- React Navigation
- Axios
- AsyncStorage

## Notes

- Ensure `JWT_SECRET` is long and random in production.
- `ddl-auto=update` is enabled for faster development.
- CORS is enabled for Expo local URLs.
