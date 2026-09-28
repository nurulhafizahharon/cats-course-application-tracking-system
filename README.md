# CATS – Course Application Tracking System

Course Application Tracking System (CATS) developed for the NUS-ISS Graduate Diploma in Systems Analysis (GDipSA), SA4105 Web Application Development Continuous Assessment.

The system manages employee training/course applications, manager approval workflows, training-day entitlements, annual training budgets, course scheduling rules, and related administration.

> **Development Status:** Work in progress.  
> The core Course Application and Manager Approval backend workflows have been implemented and tested. Employee administration and other supporting modules are currently being developed. Angular will be used for the frontend.

---

## 1. Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Bean Validation
- Maven

### Database
- MySQL
- MySQL Workbench

### API Testing
- Postman

### Frontend
- Angular — planned / next phase

### Development Tools
- Spring Tool Suite (STS)
- Git
- GitHub
- Jira

---

## 2. Application Architecture

The backend follows a layered architecture:

```text
Client / Angular
       |
       v
   Controller
       |
       v
     Service
       |
       v
   Repository
       |
       v
 JPA / Hibernate
       |
       v
      MySQL
```

DTOs and mappers are used to avoid exposing JPA entities directly through the REST API.

```text
HTTP Request
     |
     v
Request DTO
     |
     v
Controller
     |
     v
Service
     |
     v
Entity / Repository
     |
     v
MySQL

MySQL
     |
     v
Entity
     |
     v
Mapper
     |
     v
Response DTO
     |
     v
JSON Response
```

---

## 3. Project Package Structure

```text
sg.edu.nus.cats
|
|-- config
|-- controller
|-- dto
|-- entity
|-- enums
|-- exception
|-- mapper
|-- repository
`-- service
    `-- holiday
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `config` | Application configuration and development data |
| `controller` | REST API endpoints |
| `dto` | Request and response objects |
| `entity` | JPA database entities |
| `enums` | Application enums |
| `exception` | Centralised API error handling |
| `mapper` | Entity-to-DTO conversion |
| `repository` | Spring Data JPA database access |
| `service` | Business logic and validation |
| `service.holiday` | Public holiday handling |

---

# 4. Domain Model

## Employee

The `Employee` entity represents a system user/employee.

Current fields include:

- employee ID
- username
- password
- roles
- active status
- name
- date of joining
- annual training budget
- training-day entitlement
- designation
- manager

Employees may have one or more roles.

### Roles

```text
ADMIN
MANAGER
EMPLOYEE
```

An employee may also reference another `Employee` as their manager.

Example:

```text
Bob
Role: MANAGER
     |
     +---- manages ----> Alice
                         Role: EMPLOYEE
```

This avoids requiring a separate Manager entity.

---

## Training Provider

Training providers are stored separately from course applications.

Examples used during development:

- NUS-ISS
- NTUC LearningHub

A course application references its training provider.

---

## Course Catalogue Item

`CourseCatalogueItem` represents a predefined course available from a training provider.

Information includes:

- course ID
- title
- description
- category
- training provider
- active status

A course application may optionally reference a catalogue item.

This allows both catalogue and non-catalogue courses to be submitted.

---

## Course Category

Supported course categories are:

```text
INTERNAL_TRAINING
EXTERNAL_COURSE
PROFESSIONAL_CERTIFICATION
```

---

## Course Application

`CourseApplication` is the main transactional entity.

It currently contains information such as:

- application ID
- employee
- application date
- course title
- course category
- training provider
- optional course catalogue item
- start date
- end date
- calculated duration
- course fee
- half-day indicator
- justification
- work dissemination
- application status
- last updated date/time
- manager decision reason
- manager who made the decision
- decision date/time
- course experience comment

---

# 5. Course Application Status

The current application statuses include:

```text
APPLIED
UPDATED
APPROVED
REJECTED
CANCELLED
COMPLETED
DELETED
```

Typical successful lifecycle:

```text
Employee submits application
            |
            v
         APPLIED
            |
            v
     Manager approves
            |
            v
        APPROVED
            |
            v
       Course ends
            |
            v
 Employee records experience
            |
            v
        COMPLETED
```

Other possible paths include:

```text
APPLIED / UPDATED
        |
        +---- Manager rejects ----> REJECTED

APPROVED
        |
        +---- Employee cancels ---> CANCELLED

APPLIED / UPDATED
        |
        +---- Employee withdraws -> DELETED
```

---

# 6. Course Application Submission

Employees can submit course applications through the REST API.

Example:

```http
POST /api/applications?username=aliceTheEmployee
```

Example request:

```json
{
  "courseTitle": "Cloud Architecture Workshop",
  "category": "EXTERNAL_COURSE",
  "trainingProviderId": 1,
  "courseCatalogueItemId": null,
  "startDate": "2026-11-10",
  "endDate": "2026-11-11",
  "fee": 400,
  "halfDay": false,
  "justification": "Improve cloud architecture knowledge",
  "workDissemination": "Share cloud architecture practices with the team"
}
```

Submission processing includes:

```text
Find employee
      |
Find training provider
      |
Find optional catalogue item
      |
Validate request
      |
Calculate training days
      |
Check overlapping applications
      |
Check annual training-day entitlement
      |
Check annual training budget
      |
Create application
      |
Set status = APPLIED
      |
Save
```

---

# 7. Training-Day Calculation

Training duration is calculated by `TrainingDayCalculator`.

The calculation considers:

- start date
- end date
- weekends
- Singapore public holidays
- half-day rules
- course category

---

## Working Days

Saturday and Sunday are excluded from training duration.

Example:

```text
Friday       -> counted
Saturday     -> excluded
Sunday       -> excluded
Monday       -> counted
```

Result:

```text
2 training days
```

---

## Singapore Public Holidays

Holiday handling is separated through:

```text
HolidayProvider
SingaporeHolidayProvider
```

The training-day calculator therefore determines whether each date is a valid working day.

Conceptually:

```text
Date
 |
 +--> Weekend? ------> Exclude
 |
 +--> Public holiday? -> Exclude
 |
 `--> Otherwise ------> Working day
```

---

# 8. Single-Day Courses

A course may have:

```text
startDate == endDate
```

A normal valid single-day course counts as:

```text
1.0 training day
```

provided that the date is a working day.

---

# 9. Half-Day Training

Half-day training is supported for internal training.

Valid example:

```text
category  = INTERNAL_TRAINING
startDate = endDate
halfDay   = true
```

Calculated duration:

```text
0.5 day
```

Current validation prevents:

- half-day external courses
- half-day professional certification
- multi-day half-day applications

---

# 10. Course Date Validation

Course application dates are validated.

Current rules include:

- start date cannot be after end date
- start date must be in the future
- start date must be a working day
- end date must be a working day
- weekends are excluded
- Singapore public holidays are excluded

Invalid business requests return HTTP `400 Bad Request`.

---

# 11. Annual Training-Day Entitlement

Each employee has an annual training-day entitlement.

The system calculates:

```text
Annual Training-Day Entitlement
              -
Existing Qualifying Application Days
              =
Remaining Training-Day Entitlement
```

The new application is rejected if it exceeds the remaining entitlement.

Example error:

```text
Application exceeds remaining training-day entitlement.
Remaining entitlement: 1.00 day(s)
```

The logic is implemented separately in:

```text
EntitlementValidationService
```

---

# 12. Annual Training Budget

Employees also have an annual training budget.

The system calculates:

```text
Annual Training Budget
          -
Existing Qualifying Course Fees
          =
Remaining Training Budget
```

An application is rejected when its fee exceeds the remaining annual budget.

This logic is handled by:

```text
BudgetValidationService
```

---

# 13. Overlapping Course Validation

Employees cannot submit conflicting course applications.

Example:

```text
Existing course
Nov 2  ---------------- Nov 4

New course
          Nov 3 ---------------- Nov 5

Result: OVERLAP
```

The request is rejected with an appropriate validation message.

During an update, the current application is excluded from the overlap query so that an application does not conflict with itself.

---

# 14. Employee Application Operations

The employee workflow currently supports:

- submit application
- list own applications
- view application details
- update pending application
- withdraw/delete pending application
- cancel approved application
- complete approved course

---

## Update Application

Only pending applications may be updated.

The update operation re-runs the relevant business validation, including:

- request validation
- date validation
- training-day calculation
- overlap validation
- annual entitlement validation
- annual budget validation

After a successful update, the application status becomes:

```text
UPDATED
```

---

## Withdraw / Delete Application

Pending applications use soft deletion.

The database record is not physically deleted.

Instead:

```text
status = DELETED
```

This preserves the application history.

Deleted applications are excluded from the normal employee application listing.

---

## Cancel Approved Application

An employee may cancel an approved application.

```text
APPROVED
    |
    v
CANCELLED
```

This is different from deleting/withdrawing a pending application.

---

# 15. Manager Approval Workflow

Manager operations are separated from employee application operations.

A manager can:

- view pending applications from subordinates
- view application information
- approve an application
- reject an application

The application verifies that the user performing the operation has the Manager role.

---

## Manager Approval

A pending application may be approved.

```text
APPLIED / UPDATED
        |
        v
     APPROVED
```

The system records:

- manager reason
- employee/manager who decided the application
- decision date/time

---

## Manager Rejection

A pending application may also be rejected.

```text
APPLIED / UPDATED
        |
        v
     REJECTED
```

The manager's reason and decision information are recorded.

---

## Manager Reason

A manager reason is required for both:

- approval
- rejection

Example request:

```json
{
  "reason": "Training is relevant to employee development"
}
```

An empty reason produces a validation error.

---

# 16. Workflow Protection

The service prevents invalid state transitions.

Examples include:

```text
APPROVED -> update       NOT ALLOWED
APPROVED -> reject       NOT ALLOWED
REJECTED -> update       NOT ALLOWED
DELETED  -> update       NOT ALLOWED
```

A manager also cannot repeatedly decide an application that has already been approved or rejected.

---

# 17. Course Completion

An employee may mark an approved course as completed after the course has ended.

Example:

```http
PATCH /api/applications/{id}/complete?username=aliceTheEmployee
```

Example body:

```json
{
  "experienceComment": "The course improved my understanding and I can apply the concepts to future projects."
}
```

Successful lifecycle:

```text
APPROVED
    |
Course end date reached
    |
Employee submits experience comment
    |
    v
COMPLETED
```

The experience comment is required.

---

## Early Completion Protection

The system prevents completion before the course end date.

Example error:

```text
Course cannot be completed before the course end date
```

---

# 18. DTOs and Mappers

The application uses request/response DTOs rather than exposing entities directly.

Examples include:

```text
CourseApplicationRequest
CourseApplicationResponse
ManagerDecisionRequest
CompleteCourseRequest
EmployeeCreateRequest
EmployeeUpdateRequest
EmployeeResponse
```

Mappers currently include:

```text
CourseApplicationMapper
EmployeeMapper
```

The purpose is to separate:

```text
Database Entity
      |
      v
    Mapper
      |
      v
API Response DTO
```

This also prevents sensitive entity fields such as employee passwords from being returned by the API.

---

# 19. Request Validation

Jakarta Bean Validation is used for request validation.

Examples include:

```java
@NotBlank
@NotNull
@NotEmpty
@PositiveOrZero
```

This validates fields before business processing.

Example invalid request:

```json
{
  "courseTitle": "",
  "category": null,
  "fee": -100
}
```

produces field-level validation errors.

---

# 20. Global Error Handling

Centralised exception handling is implemented through:

```text
GlobalExceptionHandler
ApiErrorResponse
```

Instead of returning raw Java/Spring stack traces to the API client, validation errors are returned in a structured format.

Example:

```json
{
  "timestamp": "...",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "courseTitle": "Course title is required"
  }
}
```

---

# 21. Employee Administration – In Progress

Employee administration is currently being developed.

The following have already been prepared:

```text
Employee
EmployeeRepository
EmployeeCreateRequest
EmployeeUpdateRequest
EmployeeResponse
EmployeeMapper
```

Repository functionality includes:

```text
findByUsername(...)
findByManager(...)
existsByUsername(...)
```

The next implementation step is:

```text
EmployeeService
```

followed by the required employee administration API/controller operations.

---

# 22. Development Data

`DataInitializer` is currently used to create development/test data.

Development users include accounts representing:

```text
Admin
Manager
Employee
```

Sample training providers and catalogue items are also initialized for development/testing.

This is development support and may be revised as employee administration and authentication are completed.

---

# 23. Database Configuration

Database credentials are supplied using environment variables.

Example:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Do **not** commit actual database passwords to GitHub.

Each developer should configure the appropriate environment variables locally.

---

## Hibernate Development Mode

During active development the project may use:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
```

This recreates the schema during application lifecycle changes.

> **Warning:** `create-drop` is intended for development/testing and will remove generated data. Do not rely on it for persistent production data.

---

# 24. Current Security Status

Spring Security configuration exists as part of the project setup, but full authentication/authorization integration is not yet complete.

During backend development and Postman testing, requests currently use usernames such as:

```text
?username=aliceTheEmployee
?username=bobTheManager
```

to exercise employee/manager workflows.

This is development scaffolding.

The intended later design is to obtain the current user from Spring Security rather than trusting a username supplied by the client.

---

# 25. Postman Tests Performed

The backend has been manually tested for scenarios including:

- successful application submission
- external course submission
- internal training submission
- professional certification submission
- single-day training
- half-day internal training
- invalid half-day external training
- invalid multi-day half-day training
- weekend validation
- public holiday validation
- start date after end date
- required-field validation
- negative fee validation
- training-day calculation
- annual training-day entitlement
- annual training budget
- overlapping course dates
- employee application listing
- individual application retrieval
- update pending application
- invalid update after decision
- withdraw/delete pending application
- manager pending application listing
- manager role validation
- manager approval
- manager rejection
- approval reason validation
- rejection reason validation
- repeated manager decision prevention
- employee cancellation of approved application
- invalid cancellation
- course completion
- experience comment validation
- early course completion prevention

---

# 26. Current Implementation Status

| Feature | Status |
|---|---|
| Core Spring Boot setup | Implemented |
| MySQL / JPA integration | Implemented |
| Employee domain model | Implemented |
| Employee roles | Implemented |
| Manager relationship | Implemented |
| Training provider model | Implemented |
| Course catalogue model | Implemented |
| Course application submission | Implemented |
| Employee application retrieval | Implemented |
| Employee application update | Implemented |
| Pending application withdrawal | Implemented |
| Manager approval | Implemented |
| Manager rejection | Implemented |
| Approved application cancellation | Implemented |
| Course completion | Implemented |
| Training-day calculation | Implemented |
| Weekend exclusion | Implemented |
| Singapore holiday handling | Implemented |
| Half-day validation | Implemented |
| Annual entitlement validation | Implemented |
| Annual budget validation | Implemented |
| Course overlap validation | Implemented |
| Request validation | Implemented |
| Global API error handling | Implemented |
| Employee administration | In Progress |
| Training provider administration | Planned |
| Course catalogue administration | Planned |
| Full Spring Security integration | Planned |
| Angular frontend | Planned / Next Phase |
| Course Fee Claim | Optional / Later |

---

# 27. Recommended Code Reading Order

For team members trying to understand the implementation, the following order is recommended:

```text
1. entity/
      Employee
      CourseApplication
      CourseCatalogueItem
      TrainingProvider

2. enums/
      Role
      ApplicationStatus
      CourseCategory

3. repository/
      EmployeeRepository
      CourseApplicationRepository

4. dto/
      CourseApplicationRequest
      CourseApplicationResponse
      ManagerDecisionRequest
      CompleteCourseRequest

5. mapper/
      CourseApplicationMapper

6. service/
      TrainingDayCalculator
      ApplicationValidationService
      EntitlementValidationService
      BudgetValidationService
      CourseApplicationService
      ManagerApplicationService

7. controller/
      CourseApplicationController
      ManagerApplicationController

8. Run and test the workflow using Postman
```

---

# 28. Git Workflow

Clone the repository:

```bash
git clone <repository-url>
```

Before starting work:

```bash
git pull
```

Check changes:

```bash
git status
```

Commit changes:

```bash
git add .
git commit -m "Describe the change"
```

Push:

```bash
git push
```

For team development, feature branches should be used rather than having every developer modify `main` directly.

Example:

```bash
git checkout -b feature/employee-management
```

---

# 29. Running the Backend

Before starting the application:

1. Install Java 21.
2. Install MySQL.
3. Create/configure the required database.
4. Configure `DB_USERNAME`.
5. Configure `DB_PASSWORD`.
6. Import the Maven project into STS/IDE.
7. Allow Maven dependencies to download.
8. Run `CatsApplication`.
9. Test the REST API using Postman.

Default backend URL:

```text
http://localhost:8080
```

---

# 30. Next Development Steps

Current planned order:

```text
Employee Administration
        |
        v
Training Provider Administration
        |
        v
Course Catalogue Administration
        |
        v
Complete remaining backend/security work
        |
        v
Angular Frontend
        |
        v
Integration / End-to-End Testing
        |
        v
Optional Course Fee Claim
```

The Course Fee Claim functionality is intentionally being left until the core application is complete.

---

## Team Note

This repository is intended to provide a shared implementation and reference for the CATS team.

If you are working on another module and are unsure how to structure it, the existing Course Application implementation provides examples of:

- JPA entities and relationships
- repositories
- request/response DTOs
- mapper classes
- service-layer business logic
- REST controllers
- Jakarta validation
- custom business validation
- exception handling
- application status workflows
- pagination
- Postman API testing

Please coordinate changes through the team's agreed Git/GitHub workflow to avoid conflicting implementations.