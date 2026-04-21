# PaperTrail - Setup Guide

## Project Structure
```
papertrail/
├── backend/          # Spring Boot Java backend
├── frontend/         # React Native Expo frontend
├── infra/            # Infrastructure files
├── tools/            # Build tools (Maven)
└── docker-compose.yml
```

## Backend Setup

### Requirements
- Java 21 (JDK)
- Maven 3.9.9
- MySQL 8.0+

### Installation
1. Navigate to backend folder:
   ```bash
   cd backend
   ```

2. Copy environment file:
   ```bash
   cp .env.example .env
   ```

3. Update `.env` with your MySQL credentials:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=your_password
   DB_NAME=papertrail_db
   CORS_ALLOWED_ORIGINS=http://your-phone-ip:8081
   ```

4. Build and run:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

Backend runs on: `http://localhost:8080/api`

## Frontend Setup

### Requirements
- Node.js 18+
- npm or yarn
- Expo CLI

### Installation
1. Navigate to frontend folder:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Copy environment file:
   ```bash
   cp .env.example .env
   ```

4. Update `.env` with backend IP:
   ```
   EXPO_PUBLIC_API_BASE_URL=http://your-computer-ip:8080/api
   ```

5. Start Expo:
   ```bash
   npx expo start --clear
   ```

## Running with Docker Compose

```bash
docker-compose up -d
```

This starts:
- MySQL database
- Backend on port 8080

## Database Setup

MySQL should auto-initialize. If manual setup needed:
```bash
mysql -u root -p < backend/init.sql
```

## API Documentation

- Registration: `POST /api/auth/register`
- Login: `POST /api/auth/login`
- Get Profile: `GET /api/auth/profile`
- Add Document: `POST /api/documents`
- Get Documents: `GET /api/documents`

## Troubleshooting

**Backend won't start:**
- Check MySQL is running
- Verify `.env` has correct DB credentials
- Clear Maven cache: `mvn clean`

**Frontend can't connect:**
- Ensure backend is running on port 8080
- Update `EXPO_PUBLIC_API_BASE_URL` with correct computer IP
- Check firewall allows port 8080

**Port 8080 already in use:**
```bash
# Kill process using port 8080
lsof -ti:8080 | xargs kill -9
```

## Tech Stack

- **Backend:** Java 21, Spring Boot, Spring Security, MySQL, JPA/Hibernate, Maven
- **Frontend:** React Native, Expo, Axios, AsyncStorage
- **Authentication:** JWT
- **Notifications:** Twilio SMS, Nodemailer
