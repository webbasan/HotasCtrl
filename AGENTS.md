# AGENTS.md - Development Guidelines for HotasCtrl

## Project Overview

HotasCtrl is a Java command-line tool to control lighting and display on the Saitek/MadCatz X52/X52pro joysticks. It uses Maven for build management and JUnit 4 for testing.

## Build Commands

### Standard Build
```bash
mvn clean compile
```
Compiles the source code.

### Running Tests
```bash
mvn test
```
Runs all unit tests.

```bash
mvn test -Dtest=ArgHandlerTest
```
Runs a single test class.

```bash
mvn test -Dtest=ArgHandlerTest#testArgsLightLed
```
Runs a specific test method.

### Packaging
```bash
mvn package
```
Creates the JAR file with dependencies (`target/HotasCtrl.jar`).

### Running the Application
```bash
java -jar target/HotasCtrl.jar light all on led all on
```

## Code Style Guidelines

### General Principles
- Follow existing code conventions in the codebase
- Keep code simple and readable
- Add TODO/FIXME comments for incomplete work (e.g., `// TODO: implement suitable exception handling`)

### Naming Conventions
- **Classes**: PascalCase (e.g., `HotasCtrl`, `ArgHandlerLight`)
- **Methods**: camelCase (e.g., `doIt()`, `getValue()`)
- **Variables**: camelCase (e.g., `configuration`, `argHandler`)
- **Constants**: UPPER_SNAKE_CASE for static final fields (e.g., `FACTORY`)
- **Packages**: lowercase (e.g., `de.mundito.args`)

### Java Version
- Target Java 1.7 (as specified in pom.xml)
- Avoid Java 8+ features

### Class Structure
- Use `final` classes when not intended for extension
- Use `private` fields with public getters
- Use constructor injection for dependencies

Example:
```java
public final class HotasCtrl {
    private final Configuration configuration;
    private Hotas hotas;

    public HotasCtrl(Configuration configuration) {
        this.configuration = configuration;
    }
}
```

### Interfaces and Factories
- Use factory interfaces for dependency creation (see `ArgHandler.Factory`)
- Use static `FACTORY` field for factory instances

Example:
```java
static final Factory FACTORY = new Factory() {
    @Override
    public Parameter getParameter() {
        return Parameter.LIGHT;
    }

    public ArgHandlerLight create(final String... values) {
        return new ArgHandlerLight(values);
    }
};
```

### Import Organization
Organize imports in the following order (no explicit checkstyle, but follow this pattern):
1. Java standard library (`java.*`)
2. Third-party libraries (`org.*`, `com.*`)
3. Project packages (`de.mundito.*`)

Separate each group with a blank line. Do not use wildcards (`import java.util.*`).

### Brace Style
- Opening brace on new line for classes/methods (as seen in tests):
  ```java
  public class ArgHandlerTest
  {
  ```
- Opening brace on same line for control structures (as seen in main code):
  ```java
  if (condition) {
      // code
  }
  ```

### Error Handling
- Use `IllegalArgumentException` for invalid parameters
- Use `try-catch` blocks for IO operations
- Print stack traces to `System.err` for fatal errors
- Handle exceptions gracefully with user-friendly messages

Example:
```java
try {
    app.init();
    app.doIt();
}
catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
    e.printStackTrace(System.err);
}
finally {
    app.shutdown();
}
```

### Testing Conventions
- Use JUnit 4 annotations: `@Test`, `@Before`, `@After`
- Use assertion methods from `org.junit.Assert`:
  - `assertEquals(message, expected, actual)`
  - `assertTrue(message, condition)`
  - `assertFalse(message, condition)`
- Test class names should end with `Test` (e.g., `ArgHandlerTest`)
- Place tests in `src/test/java` matching the main package structure

Example:
```java
@Test
public void testArgsLightLed() {
    List<ArgHandler> argHandlers = ArgHandlerRegistry.readArgs("light", "all", "on", "led", "all", "on");
    Configuration configuration = new Configuration(argHandlers);
    assertTrue(configuration.isValid());
    assertEquals("Wrong number of argument handlers", 2, argHandlers.size());
}
```

### Documentation
- Add JavaDoc comments for public classes and methods
- Include author and date in class-level JavaDoc:
  ```java
  /**
   * User: webbasan Date: 23.04.15 Time: 22:28
   */
  public abstract class ArgHandler {
  }
  ```

### Thread Safety
- Be cautious with shared mutable state
- Use `final` fields where possible
- Document thread-safety assumptions in classes that manage threads

### Constants and Enums
- Use enums for fixed sets of values (see `Parameter`, `Parameter.LightSource`, `Parameter.Brightness`)
- Group related constants in enum types rather than string constants

## Project Structure

```
src/
├── main/
│   └── java/
│       └── de/mundito/
│           ├── app/           # Main application entry point
│           ├── args/           # Argument handling
│           ├── configuration/ # Configuration management
│           ├── hid/           # HID device interaction
│           └── util/          # Utility classes
└── test/
    └── java/
        └── de/mundito/        # Test packages mirror main
```

## Common Tasks

### Adding a New Argument Handler
1. Create a new class extending `ArgHandler` in `src/main/java/de/mundito/args/`
2. Implement `isValid()`, `getInvalidValueMessage()`, `parse()`, and `getParameter()`
3. Add the corresponding `Parameter` enum value
4. Register the factory in `ArgHandlerRegistry`

### Adding a New Setup Handler
1. Create a new class implementing `SetupHandler` in `src/main/java/de/mundito/hid/`
2. Implement the `setup(Hotas hotas, ArgHandler argHandler)` method
3. Register in `SetupHandlerRegistry`

## Notes
- The project uses USB4Java (v1.2.0) for HID communication
- The application can run in foreground (console input) or background (socket server on port 8079)
- The display supports max 16 characters per line
