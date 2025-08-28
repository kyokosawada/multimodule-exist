# Maven Single Module – Comprehensive Reviewer

## Table of Contents

- [Installation](#installation)
- [Maven Lifecycles](#maven-lifecycles)
- [POM Structure](#pom-structure)
- [Profiles](#profiles)
- [Repositories](#repositories)
- [Dependency Management](#dependency-management)
- [Plugins](#plugins)
- [Practical Exercise](#practical-exercise)
- [Industry Best Practices](#industry-best-practices)
- [Troubleshooting & Resources](#troubleshooting--resources)

---

## Installation

- **Prerequisites:** Java (JDK 8+ recommended, JDK 17 for latest features)
- **Download:** [Maven Downloads](https://maven.apache.org/download.cgi)
- **Setup:**
    1. Unzip Maven to desired directory
    2. Add `<maven_home>/bin` to PATH environment variable
    3. Test with `mvn -v` in terminal
- **Local Project Initialization (Recommended):**
    ```bash
    mvn archetype:generate
    ```
  This command will start an interactive wizard where you can search and select from a wide range of archetypes (project
  templates), not just the outdated quickstart. It's recommended to review the available archetypes and pick one that
  best fits your needs (e.g., `maven-archetype-java`, `maven-archetype-webapp`, etc.) for a more complete and modern
  starter project.

## Maven Lifecycles

Maven builds operate through defined lifecycles. The most important ones are:

- **default**: Handles actual build, including validation, compilation, testing, packaging, installation, and deployment
- **clean**: Cleans up files generated during previous builds
- **site**: Creates site documentation

**Key Phases (default lifecycle):**

1. `validate`: Validate the project is correct
2. `compile`: Compile source code
3. `test`: Run unit tests
4. `package`: Package compiled code (e.g., jar)
5. `verify`: Run integration tests/checks on results
6. `install`: Install artifact into the local repository
7. `deploy`: Copy final artifact to remote repo

**Lifecycle Command Examples:**

```bash
mvn clean
mvn validate
mvn package
mvn install
mvn deploy
```

## POM Structure

The Project Object Model (POM) is Maven’s configuration file (`pom.xml`). Essential elements include:

- `<groupId>`: Organizational domain or company
- `<artifactId>`: Project/module name
- `<version>`: Build version
- `<dependencies>`: Libraries required by the project
- `<build>`: Plugins, configuration, and custom build steps
- `<properties>`: Centralized variables for reuse
- `<profiles>`: Conditional build sections for flexible configurations

**Annotated Minimal Example:**

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>my-app</artifactId>
  <version>1.0.0</version>
  <properties>
    <java.version>17</java.version>
  </properties>
  <dependencies>
    <!-- Dependencies go here -->
  </dependencies>
  <build>
    <plugins>
      <!-- Plugins go here -->
    </plugins>
  </build>
  <profiles>
    <!-- Profiles go here -->
  </profiles>
</project>
```

## Profiles

Profiles allow you to customize builds for different environments or scenarios. Useful for:

- Changing dependencies
- Setting properties/variables
- Custom plugin configuration

**Profile Definition Example:**

```xml
<profiles>
  <profile>
    <id>dev</id>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <env.type>development</env.type>
    </properties>
  </profile>
  <profile>
    <id>prod</id>
    <properties>
      <env.type>production</env.type>
    </properties>
  </profile>
</profiles>
```

**Activate with:**

```bash
mvn -P prod install
```

## Repositories

- **Local Repository:** `~/.m2/repository` on your machine
- **Remote (Central) Repository:** Default is Maven Central ([search.maven.org](https://search.maven.org/))
- **Custom/Company Repo:** Define in `settings.xml` or within the POM

**Custom Repository Example:**

```xml
<repositories>
  <repository>
    <id>my-repo</id>
    <url>https://repo.example.com/maven2</url>
  </repository>
</repositories>
```

## Dependency Management

- **Scopes:** Controls when dependency is available (`compile`, `provided`, `runtime`, `test`, `system`)
- **Transitive Dependencies:** Maven automatically includes dependencies required by your dependencies
- **Version Management & BOM:** Use `<dependencyManagement>` to control versions in multi-module projects
- **Exclusions:** Prevent unwanted transitive dependencies

**Dependency Example:**

```xml
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-lang3</artifactId>
  <version>3.14.0</version>
  <scope>compile</scope>
</dependency>
```

**Exclusion Example:**

```xml
<dependency>
  <groupId>org.example</groupId>
  <artifactId>sample-lib</artifactId>
  <exclusions>
    <exclusion>
      <groupId>unwanted.group</groupId>
      <artifactId>bad-lib</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

## Plugins

Plugins extend Maven—handle compilation, packaging, testing, deployment, and more.

**Plugin Configuration Example (Shade Plugin):**

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.6.0</version>
  <configuration>
    <finalName>${project.artifactId}-${profile.classifier}</finalName>
    <transformers>
      <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
        <mainClass>com.example.Main</mainClass>
      </transformer>
    </transformers>
  </configuration>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## Practical Exercise

**Objective:**

- Learn to setup Maven
- Understand Maven lifecycles, dependencies, and plugins

**Task:**

1. Use Maven archetype to scaffold a new project
2. Add Apache Commons as a dependency (e.g., commons-lang3)
3. Refactor code to use Commons API (e.g., StringUtils)
4. Configure plugin (e.g., Shade) to create runnable jar
5. Use profiles for variable output/user settings

## Industry Best Practices

- Always declare explicit dependency versions (avoid version drift)
- Use dependency management/BOM in multi-module setups
- Keep POM neat: only relevant plugins, dependencies
- Use profiles for environment differences, not for regular build toggling
- Always add license, developer info, and useful metadata
- Use CI/CD: Integrate Maven with pipelines (GitHub Actions, Jenkins, GitLab CI)
- Leverage repository managers (Nexus, Artifactory) for large teams

## Troubleshooting & Resources

- **Common Issues:**
    - Version conflicts: Use `mvn dependency:tree`
    - Plugin not found: Check plugin/version, check remote repo settings
    - Build fails: Check phase, logs, and exceptions
- **Useful Maven Commands:**
    - `mvn dependency:tree` – view dep graph
    - `mvn help:effective-pom` – see final config
    - `mvn clean install -X` – debug build
- **Official Docs:**
    - [Maven Reference](https://maven.apache.org/guides/index.html)
    - [POM Reference](https://maven.apache.org/pom.html)
    - [Plugin Index](https://maven.apache.org/plugins/index.html)
    - [Dependency Management](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)

---

**Review and practice this guide to be industry ready for Maven usage!**
