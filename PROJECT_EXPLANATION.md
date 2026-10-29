# Pod Portal - Proof-of-Delivery Management System
## Complete Project Documentation

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [User Roles & Responsibilities](#user-roles--responsibilities)
4. [Business Workflow](#business-workflow)
5. [Technology Stack](#technology-stack)
6. [Component Roles & Responsibilities](#component-roles--responsibilities)
7. [Complete DevOps Pipeline](#complete-devops-pipeline)
8. [Security Implementation](#security-implementation)
9. [Database Design](#database-design)
10. [Deployment Architecture](#deployment-architecture)
11. [Testing Strategy](#testing-strategy)
12. [How to Use the Application](#how-to-use-the-application)
13. [Project Structure](#project-structure)
14. [Development Workflow](#development-workflow)

---

## 🎯 Project Overview

### **What is Pod Portal?**

**Pod Portal** (Proof-of-Delivery Portal) is a web-based application designed to manage the complete lifecycle of parcel deliveries - from creation to final verification. It provides a role-based system where administrators, delivery agents, and reviewers collaborate to ensure successful and verified parcel deliveries.

### **Project Purpose**

This is a **College DevOps Mini-Project** for BE Computer Engineering that demonstrates:
- Full-stack web application development
- Complete CI/CD pipeline implementation
- Infrastructure as Code (IaC) practices
- Containerization and orchestration
- Automated testing strategies
- Git workflow and version control
- Production deployment practices

### **Key Features**

✅ **Role-Based Access Control** - Three distinct user roles with specific permissions  
✅ **Delivery Status Tracking** - 5-stage workflow from creation to verification  
✅ **Proof of Delivery Capture** - Record recipient details, timestamps, and OTP  
✅ **Search & Filter** - Find deliveries by tracking number, customer, or status  
✅ **Dashboard & Analytics** - Real-time statistics and delivery counts  
✅ **Automated Testing** - Selenium-based browser tests  
✅ **Containerized Deployment** - Docker-based production environment  
✅ **CI/CD Integration** - Jenkins pipeline for automated builds and deployments  

### **Project Metadata**

- **Author**: Sahil Suresh Ghogare
- **Program**: BE Computer Engineering (CMPN A)
- **Version**: 0.1.0-SNAPSHOT
- **License**: Educational Project
- **Repository**: GitHub (private/institutional)

---

## 🏗️ System Architecture

### **High-Level Architecture**

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER LAYER                               │
│  Admin Dashboard  │  Agent Workspace  │  Reviewer Interface     │
└────────────────────┬────────────────────────────────────────────┘
                     │ HTTPS/HTTP
┌────────────────────▼────────────────────────────────────────────┐
│                   WEB APPLICATION LAYER                         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           Spring Boot Application (Tomcat)               │  │
│  │  - Controllers (MVC)                                     │  │
│  │  - Security Filter Chain (Authentication/Authorization)  │  │
│  │  - Business Logic Services                               │  │
│  │  - Thymeleaf Template Engine                             │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────────┘
                     │ JPA/JDBC
┌────────────────────▼────────────────────────────────────────────┐
│                    DATA LAYER                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │        H2 Database (File-based Persistence)              │  │
│  │  - Deliveries Table                                      │  │
│  │  - User Authentication Data                              │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### **Deployment Architecture**

```
┌─────────────────────────────────────────────────────────────────┐
│                     DEVELOPMENT MACHINE                         │
│  Developer writes code → Git commit → Git push                  │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                     GITHUB REPOSITORY                           │
│  Source code version control and collaboration                  │
└────────────────────┬────────────────────────────────────────────┘
                     │ SCM Polling (every 5 min)
┌────────────────────▼────────────────────────────────────────────┐
│                     JENKINS CI/CD SERVER                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Pipeline Stages:                                        │  │
│  │  1. Checkout code from GitHub                            │  │
│  │  2. Maven build (compile & package)                      │  │
│  │  3. Run Selenium tests                                   │  │
│  │  4. Build Docker image                                   │  │
│  │  5. Push to Docker Hub                                   │  │
│  │  6. Deploy container                                     │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│                     DOCKER HUB (Registry)                       │
│  sahilghogare/pod-portal:latest                                 │
│  sahilghogare/pod-portal:0.1.0-{build-number}                   │
└────────────────────┬────────────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────────────┐
│              PRODUCTION SERVER (Ubuntu/Linux)                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Docker Engine                                           │  │
│  │  ┌────────────────────────────────────────────────────┐ │  │
│  │  │  Pod Portal Container                              │ │  │
│  │  │  - Spring Boot App (Port 8080)                     │ │  │
│  │  │  - Volume: /app/data (persistent storage)          │ │  │
│  │  └────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│  Provisioned by Ansible                                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 👥 User Roles & Responsibilities

### **1. ADMIN (Administrator)**
**Role Code**: `ROLE_ADMIN`  
**Username**: `admin`  
**Password**: `Admin123!`

#### **Primary Responsibilities**
- Create new delivery orders in the system
- Edit delivery details (customer info, address, description)
- Assign deliveries to field agents
- Manage overall delivery workflow
- View comprehensive dashboard and statistics
- Search and filter all deliveries

#### **Access Permissions**
| Feature | Access |
|---------|--------|
| Create Deliveries | ✅ Full Access |
| Edit Deliveries | ✅ Full Access |
| View All Deliveries | ✅ Full Access |
| Assign to Agents | ✅ Full Access |
| Update Status | ❌ No Access |
| Capture Proof | ❌ No Access |
| Verify Deliveries | ❌ No Access |
| View Dashboard | ✅ Full Access |

#### **Key Endpoints**
- `/admin/home` - Admin dashboard and navigation
- `/deliveries` - List and manage all deliveries
- `/deliveries/new` - Create new delivery form
- `/deliveries/{id}/edit` - Edit delivery details
- `/admin/workflow` - Assign deliveries to agents
- `/dashboard/summary` - View statistics and analytics

#### **Typical Daily Workflow**
1. Login to system
2. Check dashboard for pending assignments
3. Create new deliveries from incoming orders
4. Assign unassigned deliveries to available agents
5. Monitor delivery progress
6. Review completed deliveries

---

### **2. AGENT (Delivery Agent / Field Worker)**
**Role Code**: `ROLE_AGENT`  
**Username**: `agent`  
**Password**: `Agent123!`

#### **Primary Responsibilities**
- View deliveries assigned to them
- Update delivery status during the delivery process
- Capture proof of delivery at customer location
- Add notes and comments about delivery
- Manage assigned delivery queue

#### **Access Permissions**
| Feature | Access |
|---------|--------|
| Create Deliveries | ❌ No Access |
| Edit Deliveries | ❌ No Access |
| View Assigned Deliveries | ✅ Full Access |
| View All Deliveries | ❌ No Access |
| Update Status | ✅ Full Access |
| Capture Proof | ✅ Full Access |
| Verify Deliveries | ❌ No Access |
| View Dashboard | ❌ No Access |

#### **Key Endpoints**
- `/agent/home` - Agent workspace and navigation
- `/agent/deliveries` - List of assigned deliveries
- `/deliveries/{id}` - View delivery details
- Status update functionality on delivery page
- Proof capture form on delivery page

#### **Typical Daily Workflow**
1. Login to system
2. Check assigned deliveries queue
3. Pick up parcels for delivery
4. Update status to "Out for Delivery"
5. Navigate to delivery locations
6. Complete deliveries and capture proof
7. Update status to "Delivered" with proof details
8. Return undeliverable items

---

### **3. REVIEWER (Proof Reviewer / Quality Controller)**
**Role Code**: `ROLE_REVIEWER`  
**Username**: `reviewer`  
**Password**: `Reviewer123!`

#### **Primary Responsibilities**
- Review proof of delivery for completed deliveries
- Verify authenticity and completeness of proof
- Approve deliveries by marking them as "Verified"
- Reject deliveries with insufficient proof
- Monitor delivery quality metrics

#### **Access Permissions**
| Feature | Access |
|---------|--------|
| Create Deliveries | ❌ No Access |
| Edit Deliveries | ❌ No Access |
| View All Deliveries | ✅ Full Access |
| Assign to Agents | ❌ No Access |
| Update Status | ❌ No Access |
| Capture Proof | ❌ No Access |
| Verify Deliveries | ✅ Full Access |
| View Dashboard | ✅ Full Access |

#### **Key Endpoints**
- `/reviewer/home` - Reviewer workspace and navigation
- `/deliveries` - List all deliveries (especially completed ones)
- `/deliveries/{id}` - View delivery details and proof
- `/dashboard/summary` - View verification statistics
- Verify/Reject functionality on delivery page

#### **Typical Daily Workflow**
1. Login to system
2. Check dashboard for pending verifications
3. Filter deliveries by "Delivered" status
4. Review each delivery's proof of delivery
5. Verify proof matches delivery details
6. Mark as "Verified" or "Rejected"
7. Add comments if rejection is needed
8. Generate quality reports

---

## 🔄 Business Workflow

### **Complete Delivery Lifecycle**

The system implements a 5-stage workflow:

```
┌──────────┐     ┌──────────┐     ┌──────────────────┐     ┌───────────┐     ┌──────────┐
│ CREATED  │ ──> │ ASSIGNED │ ──> │ OUT_FOR_DELIVERY │ ──> │ DELIVERED │ ──> │ VERIFIED │
└──────────┘     └──────────┘     └──────────────────┘     └───────────┘     └──────────┘
    (1)              (2)                   (3)                    (4)              (5)
   Admin            Admin                Agent                  Agent           Reviewer
                                                                                     │
                                                                                     v
                                                                                ┌──────────┐
                                                                                │ REJECTED │
                                                                                └──────────┘
```

### **Stage-by-Stage Breakdown**

#### **Stage 1: CREATED**
**Actor**: Admin  
**Action**: Create new delivery order

**Process**:
1. Admin logs into the system
2. Navigates to `/deliveries/new`
3. Fills in delivery form:
   - Tracking Number (unique identifier)
   - Customer Name
   - Customer Phone Number
   - Delivery Address (full address)
   - Parcel Description
   - Priority level (optional)
4. System validates tracking number uniqueness
5. Creates delivery record with status `CREATED`
6. Delivery appears in unassigned queue

**Business Rule**: Tracking numbers must be unique across the system

---

#### **Stage 2: ASSIGNED**
**Actor**: Admin  
**Action**: Assign delivery to agent

**Process**:
1. Admin goes to workflow page `/admin/workflow`
2. Views list of `CREATED` deliveries
3. Selects a delivery
4. Chooses an agent from the dropdown
5. Clicks "Assign to Agent"
6. System updates:
   - Status changes to `ASSIGNED`
   - Agent username is recorded
   - Assignment timestamp is saved
7. Agent sees delivery in their queue

**Business Rule**: Only admin can assign deliveries to agents

---

#### **Stage 3: OUT_FOR_DELIVERY**
**Actor**: Agent  
**Action**: Mark as out for delivery

**Process**:
1. Agent logs in and views `/agent/deliveries`
2. Sees list of assigned deliveries
3. Picks up parcels from warehouse/depot
4. Clicks on delivery details
5. Updates status to `OUT_FOR_DELIVERY`
6. System records:
   - Status change timestamp
   - Agent who started delivery
7. Agent begins delivery route

**Business Rule**: Agent can only update deliveries assigned to them

---

#### **Stage 4: DELIVERED**
**Actor**: Agent  
**Action**: Complete delivery and capture proof

**Process**:
1. Agent reaches delivery location
2. Delivers parcel to customer
3. Captures proof of delivery:
   - **Recipient Name**: Who received the parcel
   - **Timestamp**: Exact delivery time (auto-captured)
   - **OTP/Signature**: Verification code or digital signature
   - **Notes**: Any special conditions or comments
4. Updates status to `DELIVERED`
5. System saves all proof details
6. Delivery moves to verification queue

**Business Rule**: Proof of delivery is mandatory before marking as delivered

---

#### **Stage 5: VERIFIED or REJECTED**
**Actor**: Reviewer  
**Action**: Verify or reject proof

**Process (Verification)**:
1. Reviewer logs in and views `/deliveries`
2. Filters by status `DELIVERED`
3. Opens delivery details
4. Reviews proof of delivery:
   - Checks recipient name matches records
   - Verifies OTP/signature authenticity
   - Validates timestamp is reasonable
   - Reviews agent notes
5. If proof is acceptable:
   - Clicks "Verify"
   - Status changes to `VERIFIED`
   - Delivery marked as complete ✅
6. If proof is insufficient:
   - Clicks "Reject"
   - Status changes to `REJECTED`
   - Adds rejection reason
   - Delivery flagged for investigation ❌

**Business Rule**: Only reviewers can verify or reject deliveries

---

### **Example: Complete End-to-End Scenario**

**Scenario**: Electronics Package Delivery

**Monday 9:00 AM - Creation**
```
Admin creates delivery:
- Tracking: PKG-2024-001
- Customer: Raj Patel
- Phone: +91-9876543210
- Address: Flat 301, Palm Heights, Andheri West, Mumbai - 400053
- Description: Samsung Galaxy Phone (COD Order)
Status: CREATED
```

**Monday 10:30 AM - Assignment**
```
Admin assigns delivery:
- Assigned to Agent: Vikas Kumar (agent user)
- Assignment time: 10:30 AM
Status: ASSIGNED
```

**Monday 2:00 PM - Out for Delivery**
```
Agent Vikas Kumar:
- Picks up package from warehouse
- Updates status on mobile app
- Begins delivery route
Status: OUT_FOR_DELIVERY
```

**Monday 4:15 PM - Delivered**
```
Agent reaches customer location:
- Recipient: Raj Patel (verified ID)
- OTP: 5847 (from customer's SMS)
- Note: "Package handed directly to customer, payment received (COD)"
- Time: 4:15 PM
Status: DELIVERED
```

**Tuesday 10:00 AM - Verified**
```
Reviewer checks proof:
- Recipient name matches order ✓
- OTP is valid ✓
- Timestamp is reasonable ✓
- COD payment recorded ✓
Decision: VERIFIED
Status: VERIFIED
```

**Result**: Delivery successfully completed and verified! ✅

---

## 💻 Technology Stack

### **Backend Technologies**

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 LTS | Primary programming language |
| **Spring Boot** | 3.3.5 | Application framework and runtime |
| **Spring Web MVC** | Included | Web layer and REST controllers |
| **Spring Security** | Included | Authentication and authorization |
| **Spring Data JPA** | Included | Database access and ORM |
| **Thymeleaf** | Included | Server-side template engine |
| **Spring Boot Actuator** | Included | Health checks and monitoring |
| **Maven** | 3.9+ | Build tool and dependency management |

### **Database**

| Technology | Version | Purpose |
|------------|---------|---------|
| **H2 Database** | Latest | Embedded file-based database |
| **JDBC** | Included | Database connectivity |
| **Hibernate** | 6.x | JPA implementation and ORM |

### **Frontend**

| Technology | Purpose |
|------------|---------|
| **HTML5** | Page structure |
| **CSS3** | Styling and layout |
| **Thymeleaf** | Dynamic template rendering |
| **Bootstrap** (optional) | Responsive design |

### **Testing**

| Technology | Version | Purpose |
|------------|---------|---------|
| **JUnit 5** | Latest | Unit testing framework |
| **Spring Boot Test** | Included | Integration testing |
| **Selenium WebDriver** | 4.27.0 | Browser automation and E2E testing |

### **DevOps & Infrastructure**

| Technology | Version | Purpose |
|------------|---------|---------|
| **Docker** | Latest | Containerization |
| **Jenkins** | 2.x | CI/CD automation |
| **Ansible** | Latest | Configuration management |
| **Git** | 2.x | Version control |
| **GitHub** | Cloud | Source code hosting |
| **Docker Hub** | Cloud | Container registry |

### **Server & Runtime**

| Technology | Version | Purpose |
|------------|---------|---------|
| **Tomcat** | 10.1 | Servlet container (embedded) |
| **Eclipse Temurin JRE** | 17 | Java runtime environment |
| **Ubuntu Linux** | 20.04/22.04 | Production server OS |

---

## 🔧 Component Roles & Responsibilities

### **1. JAVA (Programming Language)**

#### **Role in Project**
Primary programming language for all application logic

#### **What it Does**
- Provides object-oriented programming foundation
- Executes all business logic
- Manages application lifecycle
- Handles data processing and transformations
- Implements security mechanisms

#### **Why Java 17 LTS?**
- Long-Term Support (LTS) version for stability
- Modern language features (records, sealed classes, pattern matching)
- Excellent performance with JIT compilation
- Strong ecosystem and library support
- Industry-standard for enterprise applications

#### **Key Components**
```java
// Controllers - Handle HTTP requests
@Controller
public class DeliveryController {
    // Process user requests
}

// Services - Business logic
@Service
public class DeliveryService {
    // Delivery operations
}

// Models - Data entities
@Entity
public class Delivery {
    // Delivery data structure
}

// Security - Authentication
@Configuration
public class SecurityConfig {
    // User authentication
}
```

---

### **2. SPRING BOOT (Application Framework)**

#### **Role in Project**
Complete application framework that powers the entire system

#### **What it Does**
- **Auto-Configuration**: Automatically configures application components
- **Dependency Injection**: Manages object creation and lifecycle
- **Embedded Server**: Includes Tomcat server (no external installation needed)
- **Production-Ready**: Built-in monitoring and health checks
- **Simplification**: Reduces boilerplate code significantly

#### **Key Features Used**

**Spring Web MVC**
```
Purpose: Handle HTTP requests and responses
- Controllers receive browser requests
- Process form submissions
- Return HTML pages or JSON data
- Manage sessions and cookies
```

**Spring Security**
```
Purpose: Authentication and authorization
- User login/logout
- Password encryption (BCrypt)
- Role-based access control
- CSRF protection
- Session management
```

**Spring Data JPA**
```
Purpose: Database operations
- Automatic CRUD operations
- Query generation from method names
- Transaction management
- Entity relationships
```

**Spring Boot Actuator**
```
Purpose: Monitoring and management
- Health check endpoint: /actuator/health
- Application metrics
- System information
```

#### **Why Spring Boot?**
- **Rapid Development**: Get application running in minutes
- **Convention over Configuration**: Sensible defaults
- **Production-Ready**: Built-in monitoring and security
- **Large Ecosystem**: Extensive library support
- **Industry Standard**: Used by millions of applications

---

### **3. TOMCAT (Web Server)**

#### **Role in Project**
Servlet container that runs the web application

#### **What it Does**
- **HTTP Server**: Listens for HTTP requests on port 8080
- **Servlet Container**: Executes Java web components (Controllers)
- **Request Processing**: Handles multiple concurrent user requests
- **Session Management**: Maintains user sessions
- **Static Content**: Serves CSS, JavaScript, images

#### **How it Works in Project**
```
1. User opens browser: http://localhost:8080/login
2. Browser sends HTTP request to Tomcat
3. Tomcat receives request on port 8080
4. Tomcat routes request to Spring Boot application
5. Spring Controller processes request
6. Response is sent back through Tomcat
7. Browser displays the page
```

#### **Embedded vs External**
```
Traditional Deployment:
- Install Tomcat separately
- Deploy WAR file to Tomcat's webapps folder
- Configure Tomcat settings
- Start Tomcat service

Spring Boot Embedded (Our Approach):
- Tomcat included in application JAR/WAR
- No separate installation needed
- Single command to run: java -jar app.war
- Configuration in application.properties
- Easier deployment and portability
```

#### **Configuration**
```properties
# application.properties
server.port=8080
server.servlet.context-path=/
```

#### **Why Tomcat?**
- **Lightweight**: Small memory footprint
- **Fast**: High-performance request processing
- **Stable**: Battle-tested for 20+ years
- **Compatible**: Full Servlet 5.0 specification support
- **Embedded**: No separate installation needed

---

### **4. DOCKER (Containerization)**

#### **Role in Project**
Packages the application with all dependencies into portable containers

#### **What it Does**
- **Containerization**: Bundles app + Java + libraries into single image
- **Isolation**: Each container runs independently
- **Portability**: "Works on my machine" → "Works everywhere"
- **Version Control**: Images are versioned and immutable
- **Resource Efficiency**: Lighter than virtual machines

#### **Docker Components in Project**

**Dockerfile**
```dockerfile
FROM eclipse-temurin:17-jre          # Base image with Java 17
WORKDIR /app                          # Set working directory
COPY target/pod-portal.war app.war   # Copy application
EXPOSE 8080                           # Expose port
ENTRYPOINT ["java", "-jar", "app.war"] # Run command
```

**Docker Image**
```
What: Snapshot of application + environment
Where: sahilghogare/pod-portal:latest (Docker Hub)
Size: ~300 MB (includes JRE + app)
Contains: Java 17 + Pod Portal WAR + configurations
```

**Docker Container**
```
What: Running instance of Docker image
Name: pod-portal
Ports: 8080 (container) → 8085 (host)
Volumes: podportal-data:/app/data (persistent storage)
Status: Running/Stopped
```

#### **Build and Run Process**
```bash
# Build Docker image
docker build -t sahilghogare/pod-portal:0.1.0 .

# Push to Docker Hub
docker push sahilghogare/pod-portal:0.1.0

# Run container
docker run -d \
  --name pod-portal \
  -p 8085:8080 \
  -v podportal-data:/app/data \
  sahilghogare/pod-portal:0.1.0

# Access application
http://localhost:8085/
```

#### **Volume Mounting**
```
Purpose: Persist data even when container is stopped/removed

Host Machine          Docker Container
/var/lib/docker/     /app/data/
volumes/podportal/   ├── podportal.mv.db (H2 database file)
                     └── logs/ (application logs)

Benefit: Data survives container restarts
```

#### **Why Docker?**
- **Consistency**: Same environment everywhere (dev, test, prod)
- **Isolation**: No conflicts with other applications
- **Scalability**: Easy to create multiple instances
- **Deployment**: Simple push/pull from registry
- **Rollback**: Easy to revert to previous version

#### **Docker vs Traditional Deployment**

| Aspect | Traditional | Docker |
|--------|------------|--------|
| Dependencies | Install manually | Bundled in image |
| Portability | "Works on my machine" | Works everywhere |
| Scaling | Complex | Simple (create more containers) |
| Updates | Risky | Easy rollback |
| Isolation | Shared resources | Isolated containers |

---

### **5. JENKINS (CI/CD Automation)**

#### **Role in Project**
Automates the entire build, test, and deployment pipeline

#### **What it Does**
- **Continuous Integration**: Automatically builds code when pushed
- **Automated Testing**: Runs unit and Selenium tests
- **Continuous Deployment**: Deploys to production automatically
- **Build Management**: Tracks build history and artifacts
- **Notification**: Alerts team on build failures

#### **Jenkins Pipeline Stages**

```groovy
pipeline {
    agent any
    
    stages {
        // STAGE 1: CHECKOUT
        stage('Checkout') {
            // Clone latest code from GitHub
            // Triggered by: SCM polling every 5 minutes
        }
        
        // STAGE 2: BUILD
        stage('Build') {
            // Command: mvn clean compile
            // Compiles Java source code
            // Downloads dependencies
            // Validates code syntax
        }
        
        // STAGE 3: PACKAGE
        stage('Package') {
            // Command: mvn package
            // Creates pod-portal.war file
            // Archives artifact for download
            // Fingerprints for tracking
        }
        
        // STAGE 4: SELENIUM TESTS
        stage('Selenium Tests') {
            // Start temporary app on port 8083
            // Run automated browser tests
            // Capture screenshots on failure
            // Stop temporary app
            // Fail build if tests fail
        }
        
        // STAGE 5: DOCKER BUILD
        stage('Docker Build') {
            // Build container image
            // Tag: sahilghogare/pod-portal:0.1.0-{BUILD_NUMBER}
            // Tag: sahilghogare/pod-portal:latest
        }
        
        // STAGE 6: DOCKER PUSH
        stage('Docker Push') {
            // Login to Docker Hub
            // Push versioned image
            // Push latest tag
        }
        
        // STAGE 7: DEPLOY
        stage('Deploy Container') {
            // Stop old container
            // Start new container
            // Mount data volume
            // Health check verification
        }
    }
    
    post {
        always {
            // Archive test results
            // Archive screenshots
            // Clean workspace
        }
        success {
            // Send success notification
        }
        failure {
            // Send failure alert
        }
    }
}
```

#### **Build Triggers**

**SCM Polling**
```
Schedule: H/5 * * * * (every 5 minutes)

Process:
1. Jenkins checks GitHub for new commits
2. If changes detected, triggers build
3. If no changes, waits for next poll
```

**Manual Trigger**
```
Admin can manually trigger build:
- Jenkins Dashboard → Project → Build Now
```

#### **Build Artifacts**

Jenkins stores several artifacts:
```
Artifacts/
├── pod-portal.war                    # Application package
├── test-results/
│   └── surefire-reports/*.xml       # JUnit test results
├── selenium-screenshots/
│   └── *.png                         # Test failure screenshots
└── logs/
    ├── selenium-app-out.log          # Application output
    └── selenium-app-err.log          # Application errors
```

#### **Environment Variables**

```groovy
environment {
    IMAGE_REPO = 'sahilghogare/pod-portal'
    VERSION = "0.1.0-${env.BUILD_NUMBER}"
}

Parameters:
- HOST_PORT: 8085 (default)
- DEPLOY_ENV: dev/test
```

#### **Why Jenkins?**
- **Automation**: Eliminates manual deployment steps
- **Reliability**: Consistent build process every time
- **Speed**: Deploys in minutes, not hours
- **Quality**: Catches issues before production
- **Traceability**: Complete build history and logs
- **Flexibility**: Highly customizable pipeline

---

### **6. UBUNTU/LINUX (Operating System)**

#### **Role in Project**
Production server operating system that hosts the Docker containers

#### **What it Does**
- **OS Platform**: Provides kernel and system services
- **Docker Host**: Runs Docker Engine
- **Network Management**: Handles network connections
- **File System**: Manages persistent storage
- **Security**: Firewall and access control

#### **System Requirements**
```
Minimum Requirements:
- CPU: 2 cores
- RAM: 4 GB
- Disk: 20 GB
- Network: Internet connectivity
- OS: Ubuntu 20.04 LTS or 22.04 LTS

Recommended:
- CPU: 4 cores
- RAM: 8 GB
- Disk: 50 GB SSD
```

#### **Installed Components**
```bash
# Docker Engine
$ docker --version
Docker version 24.0.7

# Java (for Jenkins agent, optional)
$ java -version
openjdk version "17.0.9" LTS

# Python (for Ansible)
$ python3 --version
Python 3.10.12

# Ansible
$ ansible --version
ansible 2.14.0
```

#### **Directory Structure**
```
Production Server:
/
├── home/
│   └── deploy/                     # Deployment user
│       └── .ssh/                   # SSH keys
├── opt/
│   └── podportal/                  # Application directory
│       ├── data/                   # Persistent data
│       └── podportal.env           # Environment variables
├── var/
│   ├── lib/
│   │   └── docker/                 # Docker data
│   │       └── volumes/
│   │           └── podportal-data/ # Database volume
│   └── log/
│       └── docker/                 # Container logs
└── etc/
    └── docker/                     # Docker configuration
```

#### **Ansible Provisioning**

Ubuntu server is configured using Ansible:

```yaml
Tasks:
1. Install Docker
2. Create deploy user
3. Add user to docker group
4. Create application directories
5. Set up environment files
6. Configure firewall rules
7. Enable Docker service
```

#### **Network Configuration**
```
Ports:
- 22: SSH access
- 8085: Application access (mapped from container 8080)
- 80/443: HTTP/HTTPS (optional, with reverse proxy)

Firewall Rules:
- Allow 22 (SSH)
- Allow 8085 (Application)
- Deny all other incoming
```

#### **Why Ubuntu?**
- **Stability**: LTS versions supported for 5 years
- **Docker Support**: Excellent Docker compatibility
- **Package Management**: Easy software installation (apt)
- **Community**: Large support community
- **Security**: Regular security updates
- **Free**: No licensing costs

---

### **7. H2 DATABASE (Data Persistence)**

#### **Role in Project**
Embedded relational database for storing all application data

#### **What it Does**
- **Data Storage**: Stores deliveries, users, proof records
- **Transactions**: ACID-compliant transactions
- **Queries**: SQL-based data retrieval
- **File-Based**: Persists data to disk file
- **H2 Console**: Web-based database browser

#### **Database Configuration**

**Connection Details**
```properties
# application.properties
spring.datasource.url=jdbc:h2:file:./data/podportal
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

**Database Files**
```
data/
├── podportal.mv.db              # Main database file (MV_STORE format)
└── podportal.trace.db           # Trace/log file (debugging)

Size: ~10 MB (grows with data)
Location: Relative to application working directory
Persistence: Survives application restarts
```

#### **Database Schema**

**Main Table: DELIVERY**
```sql
CREATE TABLE DELIVERY (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tracking_no VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20),
    delivery_address VARCHAR(500) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(30) NOT NULL,
    assigned_agent VARCHAR(50),
    proof_recipient VARCHAR(100),
    proof_time TIMESTAMP,
    proof_note VARCHAR(500),
    proof_otp VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_tracking_no ON DELIVERY(tracking_no);
CREATE INDEX idx_status ON DELIVERY(status);
CREATE INDEX idx_assigned_agent ON DELIVERY(assigned_agent);
CREATE INDEX idx_created_at ON DELIVERY(created_at);
```

**Example Data**
```sql
INSERT INTO DELIVERY VALUES(
    1,                                    -- id
    'PKG-2024-001',                       -- tracking_no
    'Raj Patel',                          -- customer_name
    '+91-9876543210',                     -- customer_phone
    'Flat 301, Palm Heights, Mumbai',     -- delivery_address
    'Samsung Galaxy Phone (COD)',         -- description
    'DELIVERED',                          -- status
    'agent',                              -- assigned_agent
    'Raj Patel',                          -- proof_recipient
    '2024-01-15 16:15:00',               -- proof_time
    'Package handed to customer',         -- proof_note
    '5847',                               -- proof_otp
    '2024-01-15 09:00:00',               -- created_at
    '2024-01-15 16:15:00'                -- updated_at
);
```

#### **H2 Console Access**
```
URL: http://localhost:8080/h2-console

Login:
- JDBC URL: jdbc:h2:file:./data/podportal
- Username: sa
- Password: (leave empty)

Features:
- Browse tables
- Execute SQL queries
- View database statistics
- Export/import data
```

#### **JPA Entity Mapping**
```java
@Entity
@Table(name = "DELIVERY")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_no", unique = true, nullable = false)
    private String trackingNo;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    
    // ... other fields
}
```

#### **Why H2 Database?**
- **Embedded**: No separate database server needed
- **File-Based**: Easy backup (just copy the file)
- **Fast**: In-memory mode available for testing
- **SQL Compatible**: Standard SQL syntax
- **Small**: Lightweight (2 MB JAR file)
- **Development Friendly**: H2 Console for debugging
- **Zero Configuration**: Works out of the box

#### **H2 vs Production Database**

| Feature | H2 | PostgreSQL/MySQL |
|---------|----|--------------------|
| Setup | Zero config | Requires installation |
| Performance | Good for <1M records | Better for large data |
| Concurrent Users | 1-50 users | 100+ users |
| Use Case | Development, Small apps | Production, Large apps |
| Data Volume | <10 GB | 100+ GB |

**Note**: For production with high load, H2 can be replaced with PostgreSQL or MySQL without code changes (JPA abstraction).

---

### **8. GIT & GITHUB (Version Control)**

#### **Role in Project**
Source code version control and collaboration platform

#### **What Git Does**
- **Version Control**: Tracks every code change
- **Branching**: Enables parallel development
- **History**: Complete audit trail of changes
- **Collaboration**: Multiple developers working together
- **Rollback**: Undo mistakes easily

#### **Branching Strategy (GitFlow)**

```
main (production-ready)
  │
  ├─── develop (integration branch)
  │     │
  │     ├─── feature/US-01-user-login
  │     ├─── feature/US-02-create-delivery
  │     ├─── feature/US-03-capture-proof
  │     │
  │     ├─── bugfix/12-search-empty-result
  │     ├─── bugfix/15-login-timeout
  │     │
  │     ├─── chore/add-jenkinsfile
  │     └─── docs/update-readme
  │
  └─── hotfix/1.0.1-critical-fix
```

**Branch Rules**:
- `main`: Only stable, tested code
- `develop`: Integration of completed features
- `feature/*`: New functionality
- `bugfix/*`: Bug fixes
- `chore/*`: Build/config changes
- `docs/*`: Documentation updates
- `hotfix/*`: Emergency production fixes

#### **Commit Convention**

```
Format: type: subject

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- test: Adding tests
- build: Build system
- ci: CI/CD changes
- chore: Maintenance
- refactor: Code restructuring

Examples:
✅ feat: add delivery creation form
✅ fix: resolve tracking number duplication issue
✅ docs: update API documentation
✅ test: add Selenium tests for login
✅ ci: configure Jenkins pipeline
❌ Added new feature (too vague)
❌ Fixed bug (not descriptive)
```

#### **GitHub Features Used**

**Pull Requests**
```
Process:
1. Create feature branch
2. Make changes and commit
3. Push to GitHub
4. Open Pull Request to develop
5. Add description and link issues
6. Request code review
7. CI runs automated tests
8. Reviewer approves
9. Merge to develop
10. Delete feature branch
```

**Issue Tracking**
```
Issue Types:
- User Stories (feature requests)
- Bug Reports
- DevOps Tasks

Templates:
- .github/ISSUE_TEMPLATE/user_story.md
- .github/ISSUE_TEMPLATE/bug_report.md
- .github/ISSUE_TEMPLATE/devops_task.md
```

**GitHub Actions** (Optional CI)
```yaml
# Can trigger on push/PR
- Run tests
- Build Docker image
- Deploy to staging
```

#### **Why Git & GitHub?**
- **Collaboration**: Multiple developers simultaneously
- **Backup**: Code stored remotely
- **History**: Track who changed what and when
- **Branching**: Work on features independently
- **Integration**: Connects with Jenkins, Docker Hub
- **Free**: GitHub free tier sufficient

---

### **9. ANSIBLE (Configuration Management)**

#### **Role in Project**
Automates server provisioning and configuration

#### **What Ansible Does**
- **Server Setup**: Configures Ubuntu servers automatically
- **Idempotent**: Same result every time
- **Agentless**: No software installation on target servers
- **YAML-Based**: Human-readable configuration
- **Multi-Server**: Provision multiple servers identically

#### **Ansible Playbook**

```yaml
# ansible/site.yml
---
- name: Configure Pod Portal target node
  hosts: podportal                    # Target server group
  become: true                         # Use sudo
  
  vars:
    app_user: deploy                   # Application user
    app_dir: /opt/podportal            # Installation directory
  
  tasks:
    # Task 1: Install required packages
    - name: Install required packages
      ansible.builtin.apt:
        name:
          - docker.io                  # Docker engine
          - python3-docker             # Docker Python SDK
          - curl                       # HTTP client
          - ca-certificates            # SSL certificates
        state: present
        update_cache: true
    
    # Task 2: Create deploy user
    - name: Ensure deploy user exists
      ansible.builtin.user:
        name: "{{ app_user }}"
        groups: docker                 # Add to docker group
        append: true
        create_home: true
        state: present
    
    # Task 3: Create directories
    - name: Create application directories
      ansible.builtin.file:
        path: "{{ item }}"
        state: directory
        owner: "{{ app_user }}"
        group: "{{ app_user }}"
        mode: "0755"
      loop:
        - "{{ app_dir }}"              # /opt/podportal
        - "{{ app_dir }}/data"         # /opt/podportal/data
    
    # Task 4: Create environment file
    - name: Create application environment file
      ansible.builtin.copy:
        dest: "{{ app_dir }}/podportal.env"
        content: "JAVA_TOOL_OPTIONS=-Xmx384m\n"
        owner: "{{ app_user }}"
        mode: "0640"
    
    # Task 5: Enable Docker service
    - name: Enable and start Docker
      ansible.builtin.service:
        name: docker
        state: started
        enabled: true
```

#### **Inventory File**

```ini
# ansible/inventory.ini
[podportal]
production-server ansible_host=192.168.1.100 ansible_user=ubuntu

[podportal:vars]
ansible_python_interpreter=/usr/bin/python3
ansible_ssh_private_key_file=~/.ssh/production-key.pem
```

#### **Running Ansible**

```bash
# Check connectivity
ansible -i inventory.ini podportal -m ping

# Run playbook (dry-run)
ansible-playbook -i inventory.ini site.yml --check

# Run playbook (actual execution)
ansible-playbook -i inventory.ini site.yml

# Run with verbose output
ansible-playbook -i inventory.ini site.yml -v
```

#### **What Gets Configured**

**After Ansible runs, the server has**:
```
✅ Docker Engine installed and running
✅ Deploy user created with docker access
✅ Application directories ready
✅ Environment files configured
✅ Firewall rules set
✅ Ready to deploy containers
```

#### **Why Ansible?**
- **Simple**: YAML syntax, easy to learn
- **Agentless**: Uses SSH, no agent installation
- **Idempotent**: Run multiple times safely
- **Fast**: Provisions server in minutes
- **Scalable**: Same playbook for 1 or 100 servers
- **Documentation**: Playbook documents server setup

---

### **10. MAVEN (Build Tool)**

#### **Role in Project**
Build automation and dependency management

#### **What Maven Does**
- **Dependency Management**: Downloads required libraries
- **Build Lifecycle**: Compiles, tests, packages application
- **Project Structure**: Standardized directory layout
- **Plugin System**: Extensible build process
- **Repository**: Central repository for Java libraries

#### **Maven Lifecycle**

```bash
# Clean: Remove previous builds
mvn clean

# Compile: Compile source code
mvn compile
├── Downloads dependencies
├── Compiles Java files
└── Output: target/classes/

# Test: Run unit tests
mvn test
├── Compiles test code
├── Runs JUnit tests
└── Output: target/surefire-reports/

# Package: Create WAR file
mvn package
├── Runs compile
├── Runs tests
├── Creates WAR file
└── Output: target/pod-portal.war

# Full build
mvn clean package
└── Complete build from scratch
```

#### **Project Object Model (pom.xml)**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <!-- Project Metadata -->
    <groupId>edu.podportal</groupId>
    <artifactId>pod-portal</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    
    <!-- Parent: Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
    </parent>
    
    <!-- Dependencies -->
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- More dependencies... -->
    </dependencies>
    
    <!-- Build Configuration -->
    <build>
        <finalName>pod-portal</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
    
    <!-- Selenium Test Profile -->
    <profiles>
        <profile>
            <id>selenium</id>
            <!-- Run Selenium tests -->
        </profile>
    </profiles>
</project>
```

#### **Why Maven?**
- **Standard**: Industry-standard build tool
- **Dependencies**: Automatic dependency resolution
- **Reproducible**: Same build everywhere
- **Integration**: Works with Jenkins, IDEs
- **Plugins**: Extensive plugin ecosystem

---

## 🔒 Security Implementation

### **Authentication Mechanism**

**Technology**: Spring Security with BCrypt password encryption

**Process Flow**:
```
1. User enters credentials on /login page
2. Spring Security intercepts the request
3. UserDetailsService loads user from in-memory store
4. PasswordEncoder compares encrypted passwords
5. If valid:
   - Create Authentication object
   - Store in SecurityContext
   - Create HTTP session
   - Redirect to role-specific home
6. If invalid:
   - Redirect to /login?error
   - Display error message
```

**Password Encryption**:
```java
// Passwords stored as BCrypt hash
admin: $2a$10$... (Admin123! hashed)
agent: $2a$10$... (Agent123! hashed)
reviewer: $2a$10$... (Reviewer123! hashed)

// Cannot be decrypted, only verified
```

### **Authorization (Role-Based Access Control)**

**Security Configuration**:
```java
http.authorizeHttpRequests(auth -> auth
    // Public pages
    .requestMatchers("/", "/login", "/actuator/health").permitAll()
    
    // Admin-only pages
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/deliveries").hasRole("ADMIN")
    
    // Agent-only pages
    .requestMatchers("/agent/**").hasRole("AGENT")
    
    // Reviewer-only pages
    .requestMatchers("/reviewer/**").hasRole("REVIEWER")
    
    // Admin and Reviewer can view deliveries
    .requestMatchers("/deliveries/**").hasAnyRole("ADMIN", "REVIEWER")
    
    // All other pages require authentication
    .anyRequest().authenticated()
);
```

### **Session Management**

```
Session Storage: HTTP Cookie (JSESSIONID)
Session Timeout: 30 minutes (default)
CSRF Protection: Enabled (form token validation)
Remember Me: Disabled (can be enabled)
```

### **Security Best Practices Implemented**

✅ **Password Encryption**: BCrypt with salt  
✅ **CSRF Protection**: Automatic token validation  
✅ **Session Management**: Secure session cookies  
✅ **Role-Based Access**: Fine-grained permissions  
✅ **SQL Injection Prevention**: JPA parameterized queries  
✅ **XSS Prevention**: Thymeleaf escapes HTML  
✅ **HTTPS Ready**: Easy to enable SSL  

---

## 🗄️ Database Design

### **Entity-Relationship Diagram**

```
┌─────────────────────────────────────────────────────────┐
│                       DELIVERY                          │
├─────────────────────────────────────────────────────────┤
│ PK  id                    BIGINT                        │
│ UQ  tracking_no           VARCHAR(50)   NOT NULL        │
│     customer_name         VARCHAR(100)  NOT NULL        │
│     customer_phone        VARCHAR(20)                   │
│     delivery_address      VARCHAR(500)  NOT NULL        │
│     description           VARCHAR(500)                  │
│     status                VARCHAR(30)   NOT NULL        │
│     assigned_agent        VARCHAR(50)                   │
│     proof_recipient       VARCHAR(100)                  │
│     proof_time            TIMESTAMP                     │
│     proof_note            VARCHAR(500)                  │
│     proof_otp             VARCHAR(20)                   │
│     created_at            TIMESTAMP     DEFAULT NOW()   │
│     updated_at            TIMESTAMP     DEFAULT NOW()   │
└─────────────────────────────────────────────────────────┘
```

### **Field Descriptions**

| Field | Type | Purpose | Constraints |
|-------|------|---------|-------------|
| `id` | BIGINT | Primary key | Auto-increment |
| `tracking_no` | VARCHAR(50) | Unique tracking identifier | Unique, not null |
| `customer_name` | VARCHAR(100) | Recipient name | Not null |
| `customer_phone` | VARCHAR(20) | Contact number | Optional |
| `delivery_address` | VARCHAR(500) | Full delivery address | Not null |
| `description` | VARCHAR(500) | Parcel description | Optional |
| `status` | VARCHAR(30) | Current delivery status | Not null, enum |
| `assigned_agent` | VARCHAR(50) | Agent username | Null if unassigned |
| `proof_recipient` | VARCHAR(100) | Who received parcel | Null until delivered |
| `proof_time` | TIMESTAMP | When delivered | Null until delivered |
| `proof_note` | VARCHAR(500) | Delivery notes | Optional |
| `proof_otp` | VARCHAR(20) | OTP or signature | Optional |
| `created_at` | TIMESTAMP | Record creation time | Auto-set |
| `updated_at` | TIMESTAMP | Last modification time | Auto-update |

### **Indexes**

```sql
-- Performance indexes
CREATE INDEX idx_tracking_no ON DELIVERY(tracking_no);
CREATE INDEX idx_status ON DELIVERY(status);
CREATE INDEX idx_assigned_agent ON DELIVERY(assigned_agent);
CREATE INDEX idx_created_at ON DELIVERY(created_at DESC);

-- Full-text search (if needed)
-- CREATE INDEX idx_customer_search ON DELIVERY(customer_name, delivery_address);
```

### **Sample Queries**

```sql
-- Find all pending assignments
SELECT * FROM DELIVERY 
WHERE status = 'CREATED' 
ORDER BY created_at ASC;

-- Get agent's deliveries
SELECT * FROM DELIVERY 
WHERE assigned_agent = 'agent' 
  AND status IN ('ASSIGNED', 'OUT_FOR_DELIVERY')
ORDER BY created_at ASC;

-- Deliveries pending verification
SELECT * FROM DELIVERY 
WHERE status = 'DELIVERED' 
ORDER BY proof_time DESC;

-- Dashboard statistics
SELECT status, COUNT(*) as count 
FROM DELIVERY 
GROUP BY status;

-- Search by tracking number
SELECT * FROM DELIVERY 
WHERE tracking_no LIKE '%PKG%';
```

---

## 🚀 Complete DevOps Pipeline

### **Pipeline Visualization**

```
┌──────────────┐
│  Developer   │
│  Local Code  │
└──────┬───────┘
       │ git push
┌──────▼────────────────────────────────────────────────────┐
│                    GITHUB REPOSITORY                      │
│  ┌─────────┐  ┌─────────┐  ┌──────────┐  ┌───────────┐ │
│  │  main   │  │ develop │  │ feature/ │  │  bugfix/  │ │
│  └─────────┘  └─────────┘  └──────────┘  └───────────┘ │
└──────┬────────────────────────────────────────────────────┘
       │ SCM Poll (every 5 min)
┌──────▼────────────────────────────────────────────────────┐
│               JENKINS CI/CD SERVER                        │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Stage 1: Checkout                                  │  │
│  │  └─ Clone code from GitHub                         │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 2: Build                                     │  │
│  │  └─ mvn clean compile                              │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 3: Package                                   │  │
│  │  └─ mvn package → pod-portal.war                   │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 4: Selenium Tests                            │  │
│  │  ├─ Start temp app (port 8083)                     │  │
│  │  ├─ Run browser tests                              │  │
│  │  └─ Stop temp app                                  │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 5: Docker Build                              │  │
│  │  └─ docker build -t pod-portal:version             │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 6: Docker Push                               │  │
│  │  └─ docker push to Docker Hub                      │  │
│  ├────────────────────────────────────────────────────┤  │
│  │ Stage 7: Deploy                                    │  │
│  │  └─ docker run pod-portal:version                  │  │
│  └────────────────────────────────────────────────────┘  │
└──────┬────────────────────────────────────────────────────┘
       │
┌──────▼────────────────────────────────────────────────────┐
│                    DOCKER HUB                             │
│  sahilghogare/pod-portal:0.1.0-15                        │
│  sahilghogare/pod-portal:latest                          │
└──────┬────────────────────────────────────────────────────┘
       │ docker pull
┌──────▼────────────────────────────────────────────────────┐
│              PRODUCTION SERVER (Ubuntu)                   │
│  ┌────────────────────────────────────────────────────┐  │
│  │  Docker Container: pod-portal                      │  │
│  │  ├─ Port: 8085 → 8080                              │  │
│  │  ├─ Volume: /app/data                              │  │
│  │  └─ Status: Running                                │  │
│  └────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────┘
       │
┌──────▼────────────────────────────────────────────────────┐
│                      END USERS                            │
│  http://production-server:8085/                          │
└───────────────────────────────────────────────────────────┘
```

### **Pipeline Timing**

```
Typical Build Duration:

Checkout:         10 seconds
Build:            30 seconds
Package:          20 seconds
Selenium Tests:   120 seconds (2 minutes)
Docker Build:     45 seconds
Docker Push:      60 seconds (depends on network)
Deploy:           30 seconds
─────────────────────────────
Total:            ~5 minutes
```

### **Pipeline Failure Handling**

```
If Stage Fails:
├─ Build Fails
│  └─ Compilation errors → Fix code, commit, push
├─ Selenium Tests Fail
│  ├─ View screenshots in Jenkins
│  ├─ Check application logs
│  └─ Fix issue, commit, push
├─ Docker Build Fails
│  └─ Check Dockerfile syntax
└─ Deploy Fails
   ├─ Check container logs
   └─ Verify port availability
```

---

## 📁 Project Structure

```
pod-portal/
│
├── .github/                          # GitHub configuration
│   ├── ISSUE_TEMPLATE/               # Issue templates
│   │   ├── bug_report.md
│   │   ├── devops_task.md
│   │   └── user_story.md
│   └── pull_request_template.md      # PR template
│
├── .git/                             # Git repository (hidden)
│
├── ansible/                          # Ansible playbooks
│   ├── inventory.ini                 # Target servers
│   └── site.yml                      # Main playbook
│
├── data/                             # H2 database files
│   ├── podportal.mv.db               # Database file
│   └── podportal.trace.db            # Trace log
│
├── docs/                             # Project documentation
│   ├── 01-problem-definition-and-scope.docx
│   ├── 02-agile-planning-and-devops-workflow.docx
│   ├── 03-requirements-architecture-and-setup.docx
│   └── BRANCH_POLICY.md              # Git workflow
│
├── src/
│   ├── main/
│   │   ├── java/edu/podportal/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java           # Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java           # Login/logout
│   │   │   │   ├── DashboardController.java      # Dashboard
│   │   │   │   ├── DeliveryController.java       # Delivery CRUD
│   │   │   │   ├── HomeController.java           # Landing page
│   │   │   │   └── WorkflowController.java       # Workflow management
│   │   │   ├── model/
│   │   │   │   ├── Delivery.java                 # Delivery entity
│   │   │   │   └── DeliveryStatus.java           # Status enum
│   │   │   ├── repository/
│   │   │   │   └── DeliveryRepository.java       # Data access
│   │   │   ├── service/
│   │   │   │   └── DeliveryService.java          # Business logic
│   │   │   └── PodPortalApplication.java         # Main class
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       └── templates/                        # HTML templates
│   │           ├── index.html                    # Landing page
│   │           ├── login.html                    # Login form
│   │           ├── role-home.html                # Role home
│   │           ├── dashboard.html                # Dashboard
│   │           ├── deliveries/
│   │           │   ├── list.html                 # Delivery list
│   │           │   ├── form.html                 # Create form
│   │           │   ├── detail.html               # Delivery details
│   │           │   └── edit.html                 # Edit form
│   │           └── workflow/
│   │               └── admin.html                # Admin workflow
│   └── test/
│       └── java/edu/podportal/
│           ├── controller/                       # Controller tests
│           ├── service/                          # Service tests
│           └── selenium/                         # Selenium tests
│
├── target/                           # Build output (gitignored)
│   ├── classes/                      # Compiled classes
│   ├── test-classes/                 # Test classes
│   ├── surefire-reports/             # Test reports
│   └── pod-portal.war                # Packaged application
│
├── .gitignore                        # Git ignore rules
├── CONTRIBUTING.md                   # Contribution guidelines
├── Dockerfile                        # Docker image definition
├── Jenkinsfile                       # Jenkins pipeline
├── pom.xml                           # Maven configuration
├── PROJECT_EXPLANATION.md            # This file
└── README.md                         # Project overview
```

---

## 🔨 Development Workflow

### **Feature Development Process**

```
1. CREATE ISSUE
   └─ GitHub → Issues → New Issue
      └─ Use template: User Story / Bug / DevOps Task

2. CREATE BRANCH
   └─ git checkout develop
   └─ git pull origin develop
   └─ git checkout -b feature/US-05-dashboard

3. DEVELOP
   └─ Write code
   └─ Test locally: mvn spring-boot:run
   └─ Commit frequently: git commit -m "feat: add dashboard chart"

4. TEST
   └─ Run unit tests: mvn test
   └─ Run Selenium tests: mvn test -Pselenium
   └─ Manual testing in browser

5. PUSH
   └─ git push origin feature/US-05-dashboard

6. PULL REQUEST
   └─ GitHub → Pull Requests → New PR
   └─ Base: develop ← Compare: feature/US-05-dashboard
   └─ Fill description: "Closes #5"
   └─ Request review

7. CI/CD
   └─ Jenkins automatically:
      ├─ Builds the code
      ├─ Runs tests
      ├─ Reports status
      └─ Shows success/failure

8. CODE REVIEW
   └─ Reviewer checks code
   └─ Leaves comments
   └─ Approves or requests changes

9. MERGE
   └─ Merge Pull Request
   └─ Delete feature branch
   └─ develop branch updated

10. DEPLOY
    └─ Jenkins detects change
    └─ Runs full pipeline
    └─ Deploys to development environment
```

### **Release Process**

```
When develop is stable and ready for production:

1. CREATE RELEASE PR
   └─ GitHub → Pull Request
   └─ Base: main ← Compare: develop
   └─ Title: "Release v0.1.0"

2. FINAL TESTING
   └─ Test all features
   └─ Verify documentation
   └─ Check breaking changes

3. MERGE TO MAIN
   └─ Merge Pull Request
   └─ main branch updated

4. TAG RELEASE
   └─ git tag -a v0.1.0 -m "Release version 0.1.0"
   └─ git push origin v0.1.0

5. PRODUCTION DEPLOY
   └─ Jenkins pipeline on main branch
   └─ Deploys to production server
   └─ Sends notifications

6. POST-RELEASE
   └─ Update README with new features
   └─ Announce to team/users
   └─ Monitor for issues
```

---

## 🧪 Testing Strategy

### **Test Pyramid**

```
                 ▲
                /|\
               / | \
              /  |  \
             / E2E \           Selenium Tests (Browser)
            /-------\          - Login flow
           /  Integ  \         - Create delivery
          /-----------\        - Complete workflow
         /    Unit     \       JUnit Tests
        /_______________\      - Service logic
                                - Controller tests
                                - Repository tests
```

### **Test Types**

**1. Unit Tests (JUnit 5)**
```java
@Test
void shouldCreateDeliveryWithUniqueTrackingNumber() {
    Delivery delivery = new Delivery();
    delivery.setTrackingNo("TEST-001");
    delivery.setCustomerName("Test Customer");
    
    Delivery saved = deliveryService.create(delivery);
    
    assertNotNull(saved.getId());
    assertEquals("CREATED", saved.getStatus());
}

@Test
void shouldRejectDuplicateTrackingNumber() {
    // Test duplicate tracking number rejection
}
```

**2. Integration Tests (Spring Boot Test)**
```java
@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerIntegrationTest {
    
    @Test
    void shouldCreateDeliveryViaHTTP() {
        // Test full HTTP request/response cycle
    }
}
```

**3. Selenium Tests (Browser Automation)**
```java
@Test
@Tag("selenium")
void shouldLoginAsAdmin() {
    driver.get(baseUrl + "/login");
    driver.findElement(By.id("username")).sendKeys("admin");
    driver.findElement(By.id("password")).sendKeys("Admin123!");
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    
    assertTrue(driver.getCurrentUrl().contains("/admin/home"));
}

@Test
@Tag("selenium")
void shouldCreateDeliveryEndToEnd() {
    // Complete delivery creation workflow
}
```

### **Test Execution**

```bash
# Run unit tests only
mvn test

# Run Selenium tests
mvn test -Pselenium

# Run all tests with coverage
mvn clean verify

# Skip tests (for quick builds)
mvn package -DskipTests
```

---

## 📖 How to Use the Application

### **Quick Start Guide**

#### **Step 1: Access the Application**
```
URL: http://localhost:8080/login
```

#### **Step 2: Login as Admin**
```
Username: admin
Password: Admin123!
```

#### **Step 3: Create a Delivery**
```
1. Click "Deliveries" → "New Delivery"
2. Fill form:
   - Tracking No: PKG-001
   - Customer: John Doe
   - Phone: 555-1234
   - Address: 123 Main St, Mumbai
   - Description: Laptop Computer
3. Click "Create"
4. Delivery created with status: CREATED
```

#### **Step 4: Assign to Agent**
```
1. Go to "Workflow"
2. Find PKG-001
3. Select agent from dropdown
4. Click "Assign"
5. Status changes to: ASSIGNED
```

#### **Step 5: Login as Agent**
```
Logout → Login as:
Username: agent
Password: Agent123!
```

#### **Step 6: Update Status**
```
1. View "Assigned Deliveries"
2. Click on PKG-001
3. Click "Start Delivery"
4. Status: OUT_FOR_DELIVERY
```

#### **Step 7: Capture Proof**
```
1. Complete delivery to customer
2. Fill proof form:
   - Recipient: John Doe
   - OTP: 1234
   - Note: Delivered successfully
3. Click "Mark as Delivered"
4. Status: DELIVERED
```

#### **Step 8: Login as Reviewer**
```
Logout → Login as:
Username: reviewer
Password: Reviewer123!
```

#### **Step 9: Verify Delivery**
```
1. Go to "Deliveries"
2. Filter by status: DELIVERED
3. Click on PKG-001
4. Review proof details
5. Click "Verify"
6. Status: VERIFIED ✅
```

### **Dashboard Usage**

```
Admin/Reviewer Dashboard shows:
- Total Deliveries: 150
  ├─ CREATED: 12
  ├─ ASSIGNED: 8
  ├─ OUT_FOR_DELIVERY: 15
  ├─ DELIVERED: 25
  ├─ VERIFIED: 85
  └─ REJECTED: 5
```

---

## 🎓 Learning Outcomes

### **Technical Skills**

✅ **Full-Stack Development**
- Spring Boot framework
- MVC architecture
- Database design and JPA
- Frontend with Thymeleaf

✅ **DevOps Practices**
- CI/CD pipeline design
- Jenkins configuration
- Docker containerization
- Ansible automation

✅ **Version Control**
- Git branching strategies
- Pull request workflow
- Code review process

✅ **Testing**
- Unit testing with JUnit
- Integration testing
- Browser automation with Selenium

✅ **Security**
- Authentication and authorization
- Role-based access control
- Password encryption

### **Soft Skills**

✅ **Project Management**
- Agile methodology
- Issue tracking
- Documentation

✅ **Problem Solving**
- Debugging techniques
- Log analysis
- Troubleshooting

✅ **Collaboration**
- Team workflows
- Code review
- Communication

---

## 📞 Support & Resources

### **Project Resources**

- **Repository**: GitHub (contact author)
- **CI/CD**: Jenkins Dashboard
- **Container Registry**: Docker Hub - sahilghogare/pod-portal
- **Documentation**: `/docs` folder

### **Useful Links**

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [Jenkins User Handbook](https://www.jenkins.io/doc/book/)
- [Ansible Documentation](https://docs.ansible.com/)
- [H2 Database](https://www.h2database.com/)

### **Troubleshooting**

**Common Issues**:
```
Issue: Port 8080 already in use
Solution: Kill process or use different port
Command: netstat -ano | findstr :8080

Issue: Database locked
Solution: Close H2 Console, restart application

Issue: Jenkins build fails
Solution: Check console output, verify Maven/JDK

Issue: Docker container won't start
Solution: Check logs: docker logs pod-portal
```

---

## 🏆 Project Status & Roadmap

### **Completed (✅)**
- [x] Problem definition and scope
- [x] Agile planning and DevOps workflow
- [x] Requirements and architecture
- [x] Git repository setup
- [x] Basic Spring Boot application
- [x] Security implementation
- [x] Delivery CRUD operations
- [x] Role-based access control
- [x] Dashboard implementation
- [x] Jenkins pipeline
- [x] Docker containerization
- [x] Ansible playbooks

### **In Progress (🚧)**
- [ ] Complete MVP features
- [ ] Selenium test coverage
- [ ] Production deployment

### **Planned (📋)**
- [ ] Email notifications
- [ ] Mobile responsive design
- [ ] API documentation (Swagger)
- [ ] Performance monitoring
- [ ] Backup and recovery procedures

---

## 📄 License

This is an educational project created for academic purposes.

**Author**: Sahil Suresh Ghogare  
**Institution**: College of Engineering  
**Program**: BE Computer Engineering (CMPN A)  
**Year**: 2024

---

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- Jenkins community for CI/CD tools
- Docker for containerization platform
- Faculty advisors for project guidance
- Open source community for inspiration

---

**End of Project Explanation Document**

For questions or clarifications, please contact the project author.
