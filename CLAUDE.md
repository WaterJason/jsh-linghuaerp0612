# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

jshERP (管伊佳ERP) is a Chinese enterprise resource planning system focusing on inventory management, finance, and production management. It's built with a full-stack architecture using Spring Boot for the backend and Vue.js for the frontend.

## Architecture

### Backend Structure
- **Framework**: Spring Boot 2.0.0 with Java 8
- **Location**: `jshERP-boot/`
- **Main Application**: `src/main/java/com/jsh/erp/ErpApplication.java`
- **Database**: MySQL 5.7.33 with MyBatis/MyBatis-Plus
- **Cache**: Redis 6.2.1
- **Build Tool**: Maven 3.2.3

### Frontend Structure
- **Framework**: Vue 2.7.16 with Ant Design Vue 1.5.2
- **Location**: `jshERP-web/`
- **Build Tool**: vue-cli-service
- **UI Framework**: Based on Jeecg-Boot 2.2.0 template

### Key Backend Modules
- `controller/`: REST API endpoints
- `service/`: Business logic layer
- `datasource/entities/`: Database entities and POJOs
- `datasource/mappers/`: MyBatis mapper interfaces
- `datasource/vo/`: Value objects for data transfer
- `utils/`: Utility classes and helpers

### Key Frontend Modules
- `src/views/`: Vue components for pages
- `src/api/`: API service calls
- `src/router/`: Vue Router configuration
- `src/store/`: Vuex state management
- `src/components/`: Reusable Vue components

## Development Commands

### Backend (jshERP-boot/)
```bash
# Build and run backend
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Package application
mvn clean package

# Run tests
mvn test

# Generate MyBatis code
mvn mybatis-generator:generate
```

### Frontend (jshERP-web/)
```bash
# Install dependencies
yarn install

# Start development server
yarn serve    # Runs on http://localhost:3000

# Build for production
yarn build
```

### Docker Development
```bash
# Start infrastructure services (MySQL, Redis)
docker-compose -f docker-compose.infrastructure.yml up -d

# Start full development environment
docker-compose -f docker-compose.dev.yml up -d

# Quick start all services
./scripts/quick-start.sh

# Start local development (Docker infrastructure + host apps)
./scripts/start-local.sh
```

### Useful Scripts
- `./scripts/start-local.sh` - Start local development environment
- `./scripts/stop-local.sh` - Stop all local services
- `./scripts/status-local.sh` - Check service status
- `./scripts/logs-local.sh` - View service logs
- `./scripts/quick-start.sh` - Quick Docker environment startup

## Database Configuration

### Default Connection Settings
- **Host**: localhost:3306
- **Database**: jsh_erp
- **Username**: jsh_user / root
- **Password**: 123456
- **Redis**: localhost:6379, password: 1234abcd

### Database Initialization
- SQL files in `docker/mysql/init/` for initial setup
- Additional migration scripts in `docs/` and `sql/` directories

## Important Configuration Files

### Backend Configuration
- `jshERP-boot/src/main/resources/application.properties` - Main config
- `jshERP-boot/src/main/resources/application-local.properties` - Local development
- `jshERP-boot/src/main/resources/application-docker.properties` - Docker environment

### Frontend Configuration
- `jshERP-web/vue.config.js` - Vue CLI configuration
- `jshERP-web/package.json` - Dependencies and scripts

## Development URLs

When running locally:
- Frontend Development: http://localhost:3000
- Backend API: http://localhost:9999/jshERP-boot
- API Documentation: http://localhost:9999/jshERP-boot/doc.html
- Nginx Proxy: http://localhost:8000
- phpMyAdmin: http://localhost:8081

## Default Login Credentials
- **Super Admin**: waterxi / 123456
- **Tenant Admin**: admin / 123456

## Special Features

### Custom Modules
- **掐丝珐琅馆 (Cloisonne)**: Specialized craft production management
- **生产管理 (Production)**: Manufacturing workflow management
- **薪酬管理 (Salary)**: Payroll and compensation management
- **排班管理 (Schedule)**: Employee scheduling system

### Plugin System
The system supports a plugin architecture using springboot-plugin-framework. Plugin JARs can be loaded dynamically.

## Testing

### Backend Tests
```bash
cd jshERP-boot
mvn test
```

### Production Verification
```bash
# Run production management tests
./scripts/run_production_tests.sh
```

## Key Dependencies

### Backend
- Spring Boot 2.0.0
- MyBatis-Plus 3.0.7.1
- FastJSON 1.2.83
- Swagger 2.7.0
- Redis integration

### Frontend  
- Vue 2.7.16
- Ant Design Vue 1.5.2
- Vue Router 3.0.1
- Vuex 3.1.0
- Axios 0.18.0
- dayjs 1.8.0 (replaces moment.js)

## Environment Variables

Key environment variables for deployment:
- `SPRING_PROFILES_ACTIVE`: Environment profile (local/docker/prod)
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Database username  
- `MYSQL_PASSWORD`: Database password
- `REDIS_PASSWORD`: Redis password
- `FILE_UPLOAD_PATH`: File upload directory

## File Upload Locations
- Production: `/opt/jshERP/upload`
- Development: `./volumes/uploads`