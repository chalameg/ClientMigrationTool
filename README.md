# Client Migration Tool

A simple full-stack app that helps migrate clients from a legacy system to a new one. Built with Spring Boot and Vue.js to demonstrate clean architecture and modern development practices.

The backend is a Spring Boot API with in-memory storage, and the frontend is a Vue.js app with Vuetify for a clean UI. No databases or complex setup required—just clone and run.

## Tech Stack

**Backend:**
- Java 17 with Spring Boot 3.2.0
- Maven for dependency management
- Lombok to keep code clean
- In-memory storage (no database needed)

**Frontend:**
- Vue 3 with the Composition API
- Vuetify 3 for Material Design components
- Vite for fast development builds
- Axios for API calls

## Architecture Overview

I went with a classic layered architecture because it keeps things organized and easy to maintain:

**Backend Layers:**
- `domain/` - The Client entity (using Lombok for less boilerplate)
- `repository/` - Data access with an interface, so we can swap implementations easily
- `service/` - Business logic and migration rules
- `controller/` - REST endpoints for the frontend
- `exception/` - Custom exceptions for clear error handling

**Why interfaces?** The `OrganizationRepository` interface means we can switch from in-memory storage to a real database later without touching the service layer. Right now it uses `InMemoryClientRepository` with a `ConcurrentHashMap` for thread safety.

**Frontend Structure:**
- Two main components: one for legacy clients, one for migrated clients
- Centralized `api.js` service module with:
  - Separate `clientService` and `migrationService` for better organization
  - Axios interceptors for request/response logging and error handling
  - Environment-based configuration (can override API URL via `.env`)
  - Batch operations support for future extensions
- State management kept simple in `App.vue` (no Vuex/Pinia needed for this scale)
- Props down, events up for component communication

## Key Design Decisions

**In-Memory Storage:** I used a `ConcurrentHashMap` pre-loaded with sample clients. It's thread-safe and perfect for a demo without database setup overhead.

**Error Handling:** Custom exception classes (`ClientNotFoundException`, `ClientAlreadyMigratedException`, `ClientNotMigratedException`) provide clear, domain-specific errors. The controller maps these to proper HTTP status codes (404, 409) with JSON error messages that the frontend displays in user-friendly alerts.

**Logging:** Using SLF4J to log migration events to the console. When you migrate a client, you'll see "Migrated client {id} successfully" in the terminal.

**UI/UX:** Vuetify gives us a clean Material Design interface out of the box. Added loading states, success snackbars, and error alerts so users always know what's happening.

## Project Structure

```
inpartMigrationTool/
├── start.sh              # Startup script (Linux/Mac)
├── start.bat             # Startup script (Windows)
├── stop.sh               # Stop script (Linux/Mac)
├── backend/
│   ├── src/main/java/com/inpart/migration/
│   │   ├── domain/Client.java
│   │   ├── repository/
│   │   │   ├── OrganizationRepository.java
│   │   │   └── InMemoryClientRepository.java
│   │   ├── service/MigrationService.java
│   │   ├── controller/MigrationController.java
│   │   ├── exception/
│   │   │   ├── ClientNotFoundException.java
│   │   │   ├── ClientAlreadyMigratedException.java
│   │   │   └── ClientNotMigratedException.java
│   │   └── MigrationApplication.java
│   ├── src/main/resources/application.properties
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── LegacyClientsTable.vue
│   │   │   └── MigratedClientsTable.vue
│   │   ├── services/api.js
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## Getting Started

**You'll need:**
- Java 17+
- Node.js 18+
- Maven (or use the wrapper)

### Quick Start (Recommended)

**Run both servers with one command:**

```bash
# On Linux/Mac
./start.sh

# On Windows
start.bat
```

This automatically starts both backend and frontend servers. Press `Ctrl+C` to stop (Linux/Mac) or close the terminal windows (Windows).

**To stop servers manually (Linux/Mac):**
```bash
./stop.sh
```

### Manual Start

**Running the Backend:**

```bash
cd backend
mvn install
mvn spring-boot:run
```

The API starts on `http://localhost:9091` with these endpoints:
- `GET /api/legacy/clients` - Get non-migrated clients
- `POST /api/migrate/{id}` - Migrate a client
- `POST /api/rollback/{id}` - Rollback a migration (undo)
- `GET /api/new/clients` - Get migrated clients

**Running the Frontend:**

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5454` in your browser and you're good to go.

## Running Tests

**Backend Tests (JUnit):**
```bash
cd backend
mvn test
```

This runs unit tests for:
- `MigrationService` - Business logic tests
- `InMemoryClientRepository` - Data layer tests
- Tests cover success cases, error cases, and edge conditions

**Frontend Tests (Vitest + Vue Test Utils):**
```bash
cd frontend

# First, install dependencies if you haven't
npm install

# Run tests
npm test
```

Run tests with UI:
```bash
npm run test:ui
```

Generate coverage report:
```bash
npm run coverage
```

Frontend tests cover:
- Component rendering
- User interactions (button clicks)
- Event emissions
- Loading states
- Empty states
- Client count display

**Note:** If you encounter CSS import errors with Vuetify, the tests demonstrate the testing approach. The backend tests work out of the box with `mvn test`.

### Optional Configuration

**Frontend - Custom API URL:**  
Create a `.env` file in the frontend directory:
```bash
VITE_API_BASE_URL=http://localhost:9091/api
```

**Backend - Network Latency Simulation:**  
The backend includes an artificial delay feature to simulate real network latency (great for demos to see loading states). Edit `application.properties`:
```properties
# Set delay in milliseconds (default: 1000ms = 1 second)
migration.simulate.delay=1000

# Set to 0 to disable
migration.simulate.delay=0
```

## How It Works

Once both servers are running, you'll see a list of legacy clients in the top table. Click the "Migrate" button next to any client, and they'll move to the "Migrated Clients" table below. You'll get a success notification, and the backend console will log the migration.

Changed your mind? Click the "Undo" button in the migrated table to roll back the migration—the client will return to the legacy table.

Try migrating the same client twice—you'll get a 409 Conflict error. Try an invalid ID and you'll get a 404. All errors show up as friendly alerts in the UI.

**Quick API Test:**
```bash
# Get legacy clients
curl http://localhost:9091/api/legacy/clients

# Migrate client 1
curl -X POST http://localhost:9091/api/migrate/1

# See migrated clients
curl http://localhost:9091/api/new/clients

# Rollback migration
curl -X POST http://localhost:9091/api/rollback/1
```

## Features

- Clean layered architecture with separation of concerns
- Interface-based repository pattern for flexibility
- Proper error handling with meaningful HTTP status codes
- Lombok to reduce boilerplate
- Material Design UI that's responsive and modern
- Real-time feedback with loading states and notifications
- CORS configured for local development
- Configurable network latency simulation (perfect for demos)
- Rollback/undo functionality for reversing migrations

## Design Patterns

I used a few standard patterns to keep the code maintainable:

- **Repository Pattern** - Abstracts data access so we can swap storage implementations
- **Service Layer** - Keeps business logic separate from HTTP concerns
- **Dependency Injection** - Constructor injection throughout (Spring handles this)
- **MVC** - Model-View-Controller structure on the backend
- **Single Responsibility** - Each class has one clear job

## Testing

The project includes example tests demonstrating testing best practices:

**Backend (JUnit 5 + Mockito):**
- ✅ Unit tests for service layer with mocked dependencies
- ✅ Repository tests for data operations
- ✅ Tests for both success and error scenarios
- ✅ Custom exception handling tests

**Frontend (Vitest + Vue Test Utils):**
- ✅ Component rendering tests
- ✅ User interaction tests (button clicks, events)
- ✅ Props and state management tests
- ✅ Loading and empty state tests

Run `mvn test` in backend and `npm test` in frontend to see them in action!

## What Could Be Better

This is a demo, so I kept it simple. For a production app, you'd want:

- Real database (PostgreSQL or MySQL)
- Authentication with JWT tokens
- Input validation
- Unit and integration tests
- Better logging (maybe ELK stack)
- API documentation with Swagger
- Docker setup for easy deployment
- CI/CD pipeline
- Migration rollback feature

## Troubleshooting

**Port 9091 or 5454 already in use?**  
Change `server.port` in `application.properties` for backend, or `server.port` in `vite.config.js` for frontend. Don't forget to update the API URL in `frontend/src/services/api.js` if you change the backend port.

**Maven build failing?**  
Check you're on Java 17 with `java -version`. Try clearing the cache: `rm -rf ~/.m2/repository && mvn clean install`.

**Frontend can't connect?**  
Make sure the backend is running on port 9091 and check the browser console for CORS errors.

**npm install issues?**  
Clear the cache and try again:
```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

---

Built as a demonstration of clean architecture and modern full-stack development practices.

