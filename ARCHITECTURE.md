Architecture – TASKER (Task Architecture-driven System for Knowledge & Execution Records)

Project Goal

A desktop Task Manager application built in Java, with a JavaFX user interface and a local REST API exposed via Spring Boot.
The project prioritizes clean architecture, maintainability, and Domain-Driven Design (DDD), and is structured to be easily understood and extended by both developers and AI coding assistants (e.g. Codex).

⸻

Technology Stack

Language & Runtime
	•	Java 21 (LTS)
	•	Long-term support
	•	Modern language features with strong stability guarantees

⸻

Build & Dependency Management
	•	Maven
	•	Dependency management and build lifecycle
	•	Uses Spring Boot dependency management (BOM)

⸻

Desktop UI
	•	JavaFX (OpenJFX – Gluon)
	•	Standard Java desktop UI framework
	•	Integrated with Spring Boot at runtime
	•	Ready for future packaging with jlink / jpackage

⸻

Application Runtime & REST API
	•	Spring Boot 3.3.x
	•	REST API implemented with Spring Web (Spring MVC)
	•	Embedded server (Tomcat by default)
	•	Runs in the same JVM as the JavaFX application

⸻

Dependency Injection
	•	Spring Framework (IoC / DI)
	•	Constructor-based injection
	•	Configuration via annotations and @Configuration
	•	Spring is treated strictly as infrastructure (not part of the domain)

⸻

Persistence

Database
	•	SQLite (WAL mode)
	•	Embedded, file-based relational database
	•	Highly stable and resilient
	•	WAL mode improves read concurrency and crash safety

Connection Pool
	•	HikariCP
	•	High-performance JDBC connection pool
	•	Included by default via spring-boot-starter-jdbc
	•	Configured with a very small pool size (SQLite-friendly)

Data Access
	•	Plain JDBC
	•	Explicit SQL
	•	Full control over queries and mappings
	•	No ORM

⸻

Database Migrations
	•	Flyway
	•	Versioned schema migrations
	•	Automatic execution at application startup

⸻

JSON Serialization
	•	Jackson
	•	Included by default via spring-boot-starter-web
	•	Automatic request/response serialization (@RequestBody, @ResponseBody)
	•	Customizable via ObjectMapper if needed

⸻

Stack Summary

Layer	Technology
Language	Java 21
UI	JavaFX (Gluon)
Runtime / API	Spring Boot 3.3.x (Spring Web)
DI	Spring IoC
Database	SQLite (WAL)
Persistence	JDBC + HikariCP
Migrations	Flyway
JSON	Jackson


⸻

Testing

Testing Libraries
	•	JUnit 5 (Jupiter) – unit testing framework
	•	Mockito – mocking dependencies
	•	spring-boot-starter-test (test scope)
	•	Includes JUnit, Mockito, AssertJ, and Spring test utilities

Testing Strategy
	•	Domain layer
	•	Pure unit tests
	•	No Spring, no mocks unless strictly required
	•	Application layer
	•	Unit tests using Mockito
	•	Mock repositories and event bus
	•	Infrastructure layer
	•	Integration tests
	•	Real SQLite database + Flyway migrations
	•	API layer
	•	Controller tests using MockMvc or WebTestClient
	•	No business logic in controllers

⸻

Database Model (Relational – SQLite)

Entities
	•	users – owners of projects and tasks
	•	projects – task containers belonging to a user
	•	priorities – priority catalog (LOW, MEDIUM, HIGH)
	•	tags – project-scoped tags for categorization
	•	tasks – work items belonging to a project
	•	task_tags – many-to-many relationship between tasks and tags

Task descriptions are stored as TEXT, not BLOB.

⸻

Business Rules
	•	A task belongs to exactly one project
	•	A project belongs to exactly one user
	•	Tags are defined per project
	•	A task may have zero or more tags
	•	Task priority references the priorities table
	•	Time tracking:
	•	start_at: task start timestamp
	•	end_at: task actual end timestamp
	•	Duration can be calculated as end_at - start_at

⸻

Type Choices (SQLite)
	•	IDs: INTEGER PRIMARY KEY
	•	Timestamps: INTEGER (epoch seconds or millis)
	•	Textual data: TEXT

⸻

Initial Schema (DDL – conceptual)

users
	•	id (PK)
	•	email (TEXT, UNIQUE, NOT NULL)
	•	created_at

projects
	•	id (PK)
	•	user_id (FK → users)
	•	name
	•	created_at

priorities
	•	id (PK)
	•	code (TEXT, UNIQUE)
	•	description

tags
	•	id (PK)
	•	project_id (FK → projects)
	•	name
	•	UNIQUE (project_id, name)

tasks
	•	id (PK)
	•	project_id (FK → projects)
	•	priority_id (FK → priorities)
	•	external_code (TEXT, NULL)
(e.g. JIRA key ABC-123)
	•	title
	•	description (TEXT)
	•	start_at (INTEGER, NULL)
	•	end_at (INTEGER, NULL)
	•	created_at
	•	updated_at

task_tags
	•	task_id (FK → tasks)
	•	tag_id (FK → tags)
	•	PK (task_id, tag_id)

⸻

Recommended Indexes
	•	projects(user_id)
	•	tasks(project_id)
	•	tasks(project_id, priority_id)
	•	tasks(project_id, start_at)
	•	tasks(project_id, end_at)
	•	tasks(project_id, external_code)
	•	tags(project_id, name)

⸻

Referential Integrity
	•	PRAGMA foreign_keys = ON
	•	Use ON DELETE CASCADE consistently

⸻

Architecture (DDD + Screaming Architecture)

Layers
	•	domain
Entities, value objects, domain events, repository interfaces
	•	application
Use cases (commands/queries), orchestration, domain event publication
	•	infrastructure
JDBC repositories, SQLite configuration, Flyway, event bus implementation
	•	api
REST controllers (Spring MVC), DTOs, mappers
	•	ui
JavaFX views, controllers, and view models

⸻

Domain Event Bus
	•	Every use case emits Domain Events (e.g. TaskCreated)
	•	UI and other components subscribe to these events
	•	The domain and application layers depend only on an event bus interface
	•	Spring Application Events are used as the underlying implementation (infrastructure layer)

Design decision:
	•	A DomainEventBus (or DomainEventPublisher) interface is defined in the application layer
	•	The Spring-based implementation lives in infrastructure and delegates to ApplicationEventPublisher
	•	Spring annotations such as @EventListener are only used outside the domain

This preserves a clean DDD architecture while leveraging Spring’s mature eventing system.

⸻

Project Structure

src/main/java
└── com/davidpe/tasker
    ├── bootstrap
    │   ├── TaskerApplication.java          # Spring Boot entry point
    │   └── FxLauncher.java                 # JavaFX bootstrap
    │
    ├── api
    │   ├── task
    │   ├── project
    │   └── user
    │
    ├── application
    │   ├── bus
    │   ├── task
    │   ├── project
    │   └── user
    │
    ├── domain
    │   ├── common
    │   ├── task
    │   ├── project
    │   └── user
    │
    ├── infrastructure
    │   ├── db
    │   ├── repository
    │   └── bus
    │
    └── ui
        ├── task
        ├── project
        └── common

src/main/resources
└── db/migration
    └── V1__init.sql

The structure makes the domain and use cases obvious at first glance.

	---

	- UI wrapper documentation: `docs/ui-wrapper.md` — describes UiScreen/UiScreenFactory/UiFlowManager patterns and flows (MAIN, SETTINGS, NEW_TASK)

⸻

Recommendations & Technical Notes

Event Bus Implementation (Spring Application Events)
	•	Use Spring Application Events as an implementation detail, not as a domain dependency
	•	Domain and application code must never reference ApplicationEventPublisher
	•	Spring events are suitable for:
	•	UI refresh notifications
	•	Logging and auditing
	•	Non-critical side effects

Recommended pattern:
	•	application: DomainEventBus interface
	•	infrastructure: SpringDomainEventBus implements DomainEventBus
	•	ui / infrastructure: @EventListener subscribers

⸻

SQLite + JDBC + Transactions

When using SQLite with JDBC and Spring transactions, keep the following in mind:
	•	SQLite supports transactions, but:
	•	Only one write transaction can be active at a time
	•	WAL mode improves read concurrency but does not allow parallel writes

Recommendations:
	•	Use short-lived transactions in application use cases
	•	Avoid long-running business logic inside a transaction
	•	Prefer one use case = one transaction

Event publication considerations:
	•	Publishing domain events before commit may trigger listeners with uncommitted data
	•	Preferred approaches:
	•	Publish events after the use case completes
	•	Or use Spring’s @TransactionalEventListener(phase = AFTER_COMMIT) in listeners

Do not rely on SQLite for:
	•	High write concurrency
	•	Long-running or nested transactions

This is acceptable and expected for a desktop task manager application.

⸻

Next Steps
	1.	Implement V1__init.sql Flyway migration
	2.	Define application-layer use case interfaces
	3.	Implement the in-memory domain event bus
	4.	Bootstrap Spring Boot + JavaFX together

⸻

Appendix

Spring Boot + JavaFX Integration

The following article is a key reference for integrating Spring Boot with JavaFX in a clean and modern way:
	•	Creating Modern Desktop Apps with JavaFX and Spring Boot
https://bell-sw.com/blog/creating-modern-desktop-apps-with-javafx-and-spring-boot/

This article explains:
	•	How to bootstrap Spring Boot and JavaFX in the same JVM
	•	How to manage lifecycle and dependency injection
	•	Recommended project structure patterns for desktop applications

Another articles that should be of interest:
	•	https://jenkov.com/tutorials/javafx/stage.html

This reference should be considered authoritative guidance for the JavaFX + Spring Boot integration used in this project.