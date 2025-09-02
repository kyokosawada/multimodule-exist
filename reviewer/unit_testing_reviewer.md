# Modern Unit Testing in Java: A Comprehensive Reviewer

This guide provides a thorough overview of modern unit testing in Java, with a focus on JUnit 5 and Mockito. It covers
everything from foundational concepts to advanced techniques, helping you write clean, effective, and maintainable
tests.

## Topics Covered

* [Introduction to Unit Testing](#introduction-to-unit-testing)
* [Setting Up Your Testing Environment](#setting-up-your-testing-environment)
* [The Anatomy of a Great Unit Test](#the-anatomy-of-a-great-unit-test)
* [Writing Your First Unit Test](#writing-your-first-unit-test)
* [Organizing Tests and Best Practices](#organizing-tests-and-best-practices)
* [Test-Driven Development (TDD)](#test-driven-development-tdd)
* [Measuring Test Coverage](#measuring-test-coverage)
* [Deep Dive into JUnit 5](#deep-dive-into-junit-5)
* [Mastering Mockito for Test Isolation](#mastering-mockito-for-test-isolation)
* [Understanding Test Doubles](#understanding-test-doubles)

## Introduction to Unit Testing

Unit testing is a software development practice where the smallest testable parts of an application, called "units," are
individually and independently scrutinized for proper operation. In object-oriented programming, a unit is often an
individual method, but it can also be a class, a component, or any other isolated piece of functionality.

### Core Benefits of Unit Testing

* **Early Bug Detection:** Catch issues early in the development cycle, reducing the cost and effort required for fixes.
* **Improved Code Quality:** Writing tests encourages developers to create more modular, loosely coupled, and
  maintainable code.
* **Living Documentation:** Well-written tests serve as practical examples of how your code is intended to be used.
* **Safer Refactoring:** A comprehensive test suite acts as a safety net, allowing you to refactor and enhance your code
  with confidence.

## Setting Up Your Testing Environment

To get started with unit testing in Java, you need to add JUnit 5 and Mockito to your project. These are the
industry-standard libraries for writing tests and creating mock objects.

### Maven Dependencies (`pom.xml`)

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <junit.jupiter.version>5.10.2</junit.jupiter.version>
    <mockito.version>5.12.0</mockito.version>
</properties>

<dependencies>
    <!-- JUnit 5 for writing tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.jupiter.version}</version>
        <scope>test</scope>
    </dependency>

    <!-- Mockito for creating mock objects -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Gradle Dependencies (`build.gradle.kts`)

```kotlin
plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}
```

## The Anatomy of a Great Unit Test

A well-structured unit test is easy to read, understand, and maintain. The **Arrange-Act-Assert (AAA)** pattern is a
widely adopted convention for organizing your test methods.

* **Arrange:** Set up the test environment. This involves initializing objects, preparing dependencies (often as mocks),
  and defining the inputs for the code you are testing.
* **Act:** Execute the specific unit of code you want to test. This should typically be a single method call.
* **Assert:** Verify that the outcome of the action is correct. This involves checking the return value, the state of
  the object, or whether a specific interaction with a dependency occurred.

## Writing Your First Unit Test

JUnit 5 provides a rich set of annotations to structure your tests and manage their lifecycle.

### Basic Test Structure

Here is a simple example of a test class for a `Calculator`:

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        // Arrange: Executed before each test
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Should correctly add two positive numbers")
    void add_whenPositiveNumbers_shouldReturnCorrectSum() {
        // Act
        int result = calculator.add(2, 3);

        // Assert
        assertEquals(5, result);
    }
}
```

### Common Assertions

The `org.junit.jupiter.api.Assertions` class provides a wide range of static methods for making assertions.

* `assertEquals(expected, actual)`: Checks if two values are equal.
* `assertTrue(condition)`: Verifies that a condition is true.
* `assertFalse(condition)`: Verifies that a condition is false.
* `assertNotNull(object)`: Ensures that an object is not `null`.
* `assertThrows(expectedException, executable)`: Asserts that a specific exception is thrown.

### Testing for Exceptions

To verify that your code throws the correct exceptions under specific conditions, use `assertThrows`.

```java
@Test
@DisplayName("Should throw ArithmeticException when dividing by zero")
void divide_whenDenominatorIsZero_shouldThrowArithmeticException() {
    // Act & Assert
    Exception exception = assertThrows(ArithmeticException.class, () -> calculator.divide(1, 0));
    assertEquals("/ by zero", exception.getMessage());
}
```

### Parameterized Tests

Parameterized tests allow you to run the same test logic with multiple sets of data, making your tests more concise and
comprehensive.

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MathUtilsTest {
    @ParameterizedTest(name = "Adding {0} and {1} should equal {2}")
    @CsvSource({ "0, 0, 0", "1, 2, 3", "-1, 1, 0" })
    void add(int a, int b, int expected) {
        MathUtils math = new MathUtils();
        int result = math.add(a, b);
        assertEquals(expected, result);
    }
}
```

## Organizing Tests and Best Practices

Writing good tests is as important as writing good production code. Adhering to best practices ensures your test suite
remains valuable over time.

### The FIRST Principles

* **Fast:** Tests should run quickly to provide rapid feedback.
* **Independent/Isolated:** Tests should not depend on one another or on external factors.
* **Repeatable:** Tests should produce the same result every time they are run.
* **Self-Validating:** Tests should automatically determine their success or failure without manual inspection.
* **Timely:** Tests should be written at the appropriate time—ideally, just before the production code they validate.

### Test Naming Conventions

Use descriptive names that clearly state what is being tested. A recommended format is
`methodName_whenCondition_thenExpectedBehavior`. The `@DisplayName` annotation can be used to provide even more readable
descriptions.

### Grouping Tests with `@Nested`

For large classes, you can use `@Nested` to group related tests into inner classes, improving readability and
organization.

## Test-Driven Development (TDD)

TDD is a software development process that inverts the traditional "code first, test later" approach. It follows a
simple, iterative cycle:

1. **Red:** Write a failing test for a small piece of functionality. The test will fail because the code does not yet
   exist.
2. **Green:** Write the absolute minimum amount of code required to make the test pass.
3. **Refactor:** Clean up the code you just wrote, improving its design and readability while ensuring the test remains
   green.

This cycle promotes a focus on quality, encourages simple design, and builds a comprehensive test suite from the ground
up.

## Measuring Test Coverage

Test coverage is a metric that indicates the percentage of your production code that is executed by your tests. It helps
identify gaps in your test suite but does not guarantee the absence of bugs. The goal is to write meaningful tests, not
just to achieve a high coverage number.

### How JaCoCo Works

JaCoCo (Java Code Coverage) is the standard tool for measuring test coverage in Java projects. It operates using a *
*Java Agent**, which instruments the bytecode of your application *on-the-fly* as it's being executed by the JVM during
the test run. This instrumentation adds probes to the code to track which lines and branches are executed. After the
tests complete, JaCoCo uses this information to generate a detailed coverage report.

### Key Coverage Metrics

JaCoCo reports provide several metrics, the most important of which are:

* **Line Coverage:** The percentage of code lines that were executed by at least one test.
* **Branch Coverage:** The percentage of conditional branches (e.g., `if/else`, `switch` statements) that have been
  executed. This is often a more telling metric than line coverage.
* **Cyclomatic Complexity:** A measure of the number of linearly independent paths through a method. Higher complexity
  can indicate code that is harder to test and maintain.

### Implementing JaCoCo

JaCoCo is typically integrated into your build process using a plugin for Maven or Gradle.

#### Maven Configuration (`pom.xml`)

To implement JaCoCo with Maven, you configure the `jacoco-maven-plugin`.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.12</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

* **`prepare-agent` Goal:** This goal prepares the JaCoCo runtime agent and passes it to the test runner (like
  Surefire).
* **`report` Goal:** This goal generates the code coverage report in various formats (HTML, XML, CSV) after the tests
  have been run.

To generate the report, run `mvn clean verify`. The HTML report will be available in `target/site/jacoco/index.html`.

#### Gradle Configuration (`build.gradle.kts`)

With Gradle, you apply the JaCoCo plugin and configure the `jacocoTestReport` task.

```kotlin
plugins {
    id("java")
    id("jacoco") // Apply the JaCoCo plugin
}

// ... other configurations

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // Ensure report is generated after tests
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
}
```

* **`plugins { id("jacoco") }`:** Applies the JaCoCo plugin to your project.
* **`finalizedBy(tasks.jacocoTestReport)`:** Hooks the report generation task to run automatically after the `test` task
  completes.
* **`reports { ... }`:** Configures the formats and locations for the generated reports.

To generate the report, run `gradle test` or `gradle build`. The HTML report will be in
`build/reports/jacoco/index.html`.

### Aggregating Reports in a Multi-Module Project

In a multi-module project, the default setup generates a separate coverage report for each module. To get a unified view
of your project's total code coverage, you need to configure report aggregation.

The best practice is to create a dedicated module for aggregation.

**1. Create a `report` Module**

First, create a new module named `report`. Its only purpose will be to aggregate the coverage data.

**2. Add the Module to the Parent POM**

In your root `pom.xml`, add the new module to the `<modules>` section:

```xml
<modules>
    <module>app</module>
    <module>model</module>
    <module>service</module>
    <module>utilities</module>
    <module>report</module> <!-- Add this line -->
</modules>
```

**3. Configure the `report/pom.xml`**

The `pom.xml` for your `report` module is special. It has `<packaging>pom</packaging>` and depends on all the other
modules whose coverage you want to include.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project ...>
    <parent>
        <artifactId>advancedjava</artifactId>
        <groupId>com.exist</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>report</artifactId>
    <name>JaCoCo Aggregate Report</name>
    <packaging>pom</packaging>

    <dependencies>
        <!-- Add a dependency on every module you want in the report -->
        <dependency>
            <groupId>com.exist</groupId>
            <artifactId>app</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.exist</groupId>
            <artifactId>service</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.exist</groupId>
            <artifactId>utilities</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.exist</groupId>
            <artifactId>model</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <id>report-aggregate</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>report-aggregate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

* **Dependencies**: These are crucial. They tell the `report-aggregate` goal which modules to scan for `jacoco.exec` (
  data) and source files.
* **`report-aggregate` Goal**: This specific goal gathers all the data and source files from the declared dependencies
  and generates a single, unified report.

After running `mvn clean verify`, you can find the final combined report at
`report/target/site/jacoco-aggregate/index.html`.

### Coverage Tools

* **JaCoCo:** A popular and powerful code coverage tool for Java that integrates seamlessly with build tools like Maven
  and Gradle.

## Deep Dive into JUnit 5

JUnit 5 offers many advanced features for fine-grained control over your tests.

### Advanced Annotations

* `@Disabled`: Skips a test method or class.
* `@RepeatedTest(n)`: Executes a test multiple times.
* `@Tag`: Allows you to categorize tests into groups (e.g., "fast," "slow," "integration"), which can then be
  selectively executed.

### Conditional Test Execution

You can enable or disable tests based on specific conditions, such as the operating system or Java version.

* `@EnabledOnOs(OS.WINDOWS)`: Runs the test only on Windows.
* `@DisabledOnOs(OS.LINUX)`: Skips the test on Linux.
* `@EnabledOnJre(JRE.JAVA_11)`: Runs the test only on Java 11.
* `@EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")`: Runs the test only on a 64-bit architecture.

## Mastering Mockito for Test Isolation

Mockito is a powerful framework that allows you to create and configure mock objects, which are essential for isolating
the code you are testing from its dependencies.

### Setting Up Mockito

The `@ExtendWith(MockitoExtension.class)` annotation integrates Mockito with JUnit 5, automatically initializing any
fields annotated with `@Mock`, `@Spy`, or `@InjectMocks`.

```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock
    private MyRepository repository; // Creates a mock of MyRepository

    @InjectMocks
    private MyService service; // Creates an instance of MyService and injects the mocks
}
```

### Stubbing Method Calls

Use `when(...).thenReturn(...)` to define the behavior of a mock object.

```java
@Test
void processData_whenDataExists_shouldReturnProcessedData() {
    // Arrange
    when(repository.getData()).thenReturn("Mocked Data");

    // Act
    String result = service.processData();

    // Assert
    assertEquals("Processed: Mocked Data", result);
}
```

You can also stub exceptions: `when(repository.getData()).thenThrow(new RuntimeException());`

### Verifying Interactions

Use `verify(...)` to check if a method on a mock object was called with the expected arguments.

```java
@Test
void saveData_whenCalled_shouldInvokeRepositorySave() {
    // Act
    service.saveData("some data");

    // Assert
    verify(repository).save("some data"); // Verifies that save() was called with "some data"
    verify(repository, times(1)).save("some data"); // Verifies it was called exactly once
}
```

### Argument Matchers

Argument matchers provide flexibility when you don't need to match an exact argument value.

* `any()`: Matches any object.
* `anyString()`: Matches any `String`.
* `anyInt()`: Matches any `int`.
* `eq(value)`: Used when you need to mix matchers with literal values.

**Important:** If you use an argument matcher for one argument, you must use matchers for all arguments in that method
call.

```java
// Stubbing with a matcher
when(repository.findById(anyInt())).thenReturn(Optional.of(new User()));

// Verification with a matcher
verify(repository).save(any(User.class));
```

### Capturing Arguments with `ArgumentCaptor`

`ArgumentCaptor` allows you to capture an argument that was passed to a method on a mock so you can make more detailed
assertions on it.

```java
@Test
void registerUser_whenCalled_shouldSaveUserWithRegistrationDate() {
    // Arrange
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    // Act
    service.registerUser("John Doe");

    // Assert
    verify(repository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();
    assertEquals("John Doe", savedUser.getName());
    assertNotNull(savedUser.getRegistrationDate());
}
```

## Understanding Test Doubles

"Test Double" is a generic term for any object that stands in for a real object during a test. Mocks are a specific type
of test double.

* **Dummy:** An object that is passed around but never actually used.
* **Fake:** An object with a working implementation, but it's not suitable for production (e.g., an in-memory database).
* **Stub:** An object that provides canned answers to calls made during the test.
* **Mock:** An object that you can set expectations on, such as which methods will be called and with what arguments.
* **Spy:** A wrapper around a real object that allows you to selectively override its methods. Use spies with caution,
  as they can make tests more complex.

### Spying on Real Objects

Use `@Spy` to create a spy. This is useful when you want to test a real object but need to stub out a few of its
methods.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpyExampleTest {

    @Spy
    private List<String> list = new ArrayList<>();

    @Test
    void testSpy() {
        // The spy calls real methods by default
        list.add("one");
        list.add("two");
        verify(list).add("one");
        verify(list).add("two");
        assertEquals(2, list.size());

        // Now, stub the size() method
        // Use doReturn() for stubbing spies to avoid calling the real method
        doReturn(100).when(list).size();

        // The stubbed method is now called
        assertEquals(100, list.size());
    }
}
```

## Conclusion

Unit testing is an indispensable part of modern software development. By embracing practices like TDD and leveraging
powerful tools like JUnit 5 and Mockito, you can build more reliable, maintainable, and robust applications. A
well-crafted test suite is not just a bug-finding tool; it is an investment in the long-term health and quality of your
codebase.
