# Maven Multi Module – Comprehensive Reviewer

## Table of Contents

- [Introduction & Key Concepts](#introduction--key-concepts)
- [Setting Up a Multi-Module Project](#setting-up-a-multi-module-project)
- [Parent Versus Child Modules](#parent-versus-child-modules)
- [Directory Layout](#directory-layout)
- [Parent POM & Centralized Configuration](#parent-pom--centralized-configuration)
- [The <modules> Section](#the-modules-section)
- [Managing Dependencies](#managing-dependencies)
- [Building Multi-Modules](#building-multi-modules)
- [Archetypes for Multi-Module Setup](#archetypes-for-multi-module-setup)
- [Practical Exercise](#practical-exercise)
- [Industry Best Practices](#industry-best-practices)
- [Troubleshooting & Resources](#troubleshooting--resources)

---

## Introduction & Key Concepts

A multi-module Maven project is a parent project aggregating multiple sub-projects (modules), often for:

- Modularity and scalability
- Layered or feature-based code organization
- Independent testing/building of components
- Reusability (utilities, shared libraries)

---

## Setting Up a Multi-Module Project

- **Prerequisites:** Java (JDK 8+ recommended, JDK 17+ for latest features), Maven 3.6+
- **Create Parent Directory:**
    ```bash
    mkdir my-multi-module
    cd my-multi-module
    mvn archetype:generate # or generate pom.xml manually for parent-only
    ```
- **Child Modules:**
    - Within the parent, use `mvn archetype:generate` or direct subdir creation for each module (see Directory Layout)
    - Add modules to parent’s `<modules>` section

---

## Parent Versus Child Modules

- **Parent (Aggregator/Root):**
    - Contains a `<packaging>pom</packaging>`
    - Centralizes dependency/plugin versions, shared settings, and module metadata
    - Declares `<modules>` and sets global properties
- **Child (Submodule):**
    - Has `parent` section pointing to the root parent
    - Declares its own dependencies, plugins, and logic as needed
    - Can override parent config if necessary
- **Benefits:**
    - Consistency and DRY
    - Simplifies version management and CI/CD
    - Shared vs. module-specific build logic

---

## Directory Layout

Typical structure:

```
my-multi-module/
├── pom.xml            # Parent (aggregator)
├── app/               # Child: User Interfacing classes
│   └── pom.xml
├── model/             # Child: Entity classes (POJOs)
│   └── pom.xml
├── service/           # Child: Business logic
│   └── pom.xml
└── utilities/         # Child: Utility classes
    └── pom.xml
```

_Note: Create each submodule directory and its own pom.xml. All children should be declared in the
parent’s `<modules>`._

---

## Parent POM & Centralized Configuration

Centralize:

- **Dependency Versions:**
    - Use `<dependencyManagement>` to lock versions
    - Declare dependency once, import in children
- **Plugins:**
    - Central plugin configuration and versions
    - Override/expand in child as needed
- **Properties:**
    - Centralize JDK version, output directories, encoding, etc.

**Minimal parent pom.xml example:**

```xml
<project>
  <packaging>pom</packaging>
  <modules>
    <module>app</module>
    <module>model</module>
    <module>service</module>
    <module>utilities</module>
  </modules>
  <dependencyManagement>
    <dependencies>
      <!-- Centralized version -->
      <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.14.0</version>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <properties>
    <java.version>17</java.version>
  </properties>
  <build>
    <pluginManagement>
      <plugins>
        <!-- Shared plugins -->
      </plugins>
    </pluginManagement>
  </build>
</project>
```

---

## The <modules> Section

The `<modules>` section in the parent POM lists all child modules by directory name. This defines build order and
aggregation. Example:

```xml
<modules>
  <module>app</module>
  <module>model</module>
  <module>service</module>
  <module>utilities</module>
</modules>
```

- Order matters when modules depend on one another.
- Children can reference each other via `<dependency>` if needed, e.g. app -> service -> model

---

## Managing Dependencies

- **Centralized Management:**
    - Use `<dependencyManagement>` in parent for all shared versions
    - Children only specify `<groupId>`, `<artifactId>` to inherit version
- **Inter-Module Dependencies:**
    - Reference sibling modules as dependencies:
    ```xml
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>model</artifactId>
    </dependency>
    ```
    - Ensures correct build order and isolation between layers
- **Scopes:** Same as single-module: `compile`, `provided`, `runtime`, `test`, `system`

---

## Building Multi-Modules

- **Build All at Once:**
    ```bash
    mvn clean install        # from parent directory
    ```
- **Build Individual Module:**
    ```bash
    mvn clean install -pl app -am
    ```
    - `-pl` = project list, `-am` = also make required dependencies
- **Common Issues:**
    - Order: Children must be created in order of dependencies
    - Skipping: `-N` (no-recursion) skips children
    - Use `mvn help:effective-pom` to debug config inheritance

---

## Archetypes for Multi-Module Setup

Use archetypes for bootstrapping, e.g.:

```bash
mvn archetype:generate -DgroupId=com.example -DartifactId=my-multi-module -DarchetypeArtifactId=maven-archetype-quickstart
# Then add children manually or through IDE
```

- Not all archetypes create multi-module setups directly—build up by adding submodules as needed

---

## Practical Exercise

**Objective:** Modularize an existing advanced Java activity into multi-module, executable structure.

**Task:**

1. Create a new multi-module parent project
2. Add child modules: `app`, `model`, `service`, `utilities`
3. Setup inter-module dependencies as per logical layering (e.g. service depends on model, app depends on service)
4. Add a shared library (e.g., Apache Commons) via `<dependencyManagement>`
5. Refactor Java code into corresponding modules (move POJOs to model, business logic to service, etc.)
6. Configure the parent POM with plugin and dependency management
7. Build as a whole; verify each module/jar
8. Run final executable jar from `app`

---

## Industry Best Practices

- Centralize version/plugin management in parent POM (<dependencyManagement>, <pluginManagement>)
- Avoid circular dependencies between modules
- Use clear, layered or feature-based module separation (e.g., app->service->model->utilities)
- Prefer `pom` packaging for parent aggregator
- Reference sibling modules with correct project coordinates
- Use consistent plugin/configuration versions
- Thoroughly comment POM files, especially for customizations
- Use CI/CD to automate testing and building of all modules
- Leverage repository managers for internal artifacts
- Add comprehensive README and module Javadocs

---

## Troubleshooting & Resources

**Common Issues:**

- Version/Plugin Conflicts: Use `mvn help:effective-pom` and `mvn dependency:tree`
- Build Fails: Check module order, parent reference, module declarations
- Artifact Not Found: `mvn clean install` from parent to install all modules
- Incorrect Inheritance: Ensure all children point to correct parent version/coordinates

**Useful Maven Commands:**

- `mvn clean install -N`             # Build only aggregator
- `mvn clean install -pl model -am`  # Build one module and its dependencies
- `mvn help:effective-pom`           # See inheritance
- `mvn dependency:tree`              # Check dependencies

**Official Docs & References:**

- [Maven: Guide to Multiple Modules](https://maven.apache.org/guides/mini/guide-multiple-modules.html)
- [Baeldung: Multi-Module Maven Projects](https://www.baeldung.com/maven-multi-module)
- [Medium: Maven Multi-Module Guide](https://medium.com/@khileshsahu2007/maven-multi-module-project-guide-structure-examples-real-world-challenges-d867ff22a0a8)
- [Managing Dependencies](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)

---

**Carefully review this guide, build a modular advanced Java project, and become industry-ready for enterprise Maven
setups!**
