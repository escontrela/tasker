# AGENTS.md

## Project Snapshot
- Stack: JavaFX desktop UI + Spring Boot API + SQLite + Flyway + Maven.
- Source of truth for versions/plugins: `pom.xml`.
- Current Java target in `pom.xml`: `22`.

## Workflows

### 1) Local Dev Loop
1. Validate JDK:
   - `java -version`
2. Fast compile check:
   - `mvn -q -DskipTests compile`
3. Run tests:
   - `mvn test`
4. Run application:
   - `mvn spring-boot:run`

### 2) UI/FXML Change Workflow
1. Edit FXML under `src/main/resources/com/davidpe/tasker/ui/`.
2. Keep `fx:id` in lowerCamelCase and aligned with controller fields.
3. Update matching controller in `src/main/java/com/davidpe/tasker/application/ui/`.
4. Verify with:
   - `mvn -q -DskipTests compile`
   - `mvn test` (for regressions)

### 3) Database Migration Workflow
1. Add migration script in `src/main/resources/db/migration/` using Flyway naming:
   - `V{number}__{description}.sql`
2. Keep migrations forward-only (no in-place edits of already-applied versions).
3. Start app to apply migration:
   - `mvn spring-boot:run`
4. Confirm schema/data behavior through tests or app flow.

### 4) Feature Delivery Workflow
1. Implement domain/application/infrastructure/api/ui changes in their respective layers.
2. Keep controllers thin; place business logic outside UI controllers.
3. Run:
   - `mvn -q -DskipTests compile`
   - `mvn test`
4. Do a final runnable check:
   - `mvn spring-boot:run`

## Command Reference

### Build & Test
- Compile only: `mvn -q -DskipTests compile`
- Full build: `mvn clean verify`
- Unit/integration tests: `mvn test`
- Single test class: `mvn -Dtest=ClassName test`

### Run
- Run app: `mvn spring-boot:run`
- Package jar: `mvn clean package`
- Run packaged jar: `java -jar target/tasker-0.0.1-SNAPSHOT.jar`

### Diagnostics
- Show dependency tree: `mvn dependency:tree`
- Show effective pom: `mvn help:effective-pom`
- Check local SQLite file exists: `ls -l tasker.db`

## Guardrails
- Do not commit secrets or machine-specific paths.
- Do not edit generated build artifacts under `target/`.
- Prefer small, focused changes and keep architecture boundaries clear.
