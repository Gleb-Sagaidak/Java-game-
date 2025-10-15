# Terroria

Small educational Java project. 

> **Tech**: Java 23 • Maven • Local dependency: FastNoiseLite

---

## Contents
- [Overview](#overview)
- [Tech stack](#tech-stack)
- [Requirements](#requirements)
- [Local dependency (FastNoiseLite)](#local-dependency-fastnoiselite)
- [Build](#build)
- [Run](#run)
- [Project structure](#project-structure)
- [Origin & attribution](#origin--attribution)
- [Notes](#notes)
- [Roadmap](#roadmap)
- [Author](#author)

---

## Overview
Terroria is a small, personal learning project — a 2D platformer prototype where I practiced structuring a Java game from scratch and turning ideas into something playable. The codebase is intentionally simple and readable: one clear entry point (cz.cvut.game.terroria.Main), a lightweight game loop, and straightforward separation between core logic, rendering, and resources.

The project was inspired by the excellent PlatformerTutorial by KaarinGaming, but I treated it as a sandbox to try out my own experiments. In particular, I integrated FastNoiseLite as a local Maven dependency to play with procedural generation and to get a feel for managing non-centralized libraries in a Maven workflow.

---

## Tech stack
- **Language:** Java **23**
- **Build:** Maven
- **Testing:** JUnit 5
- **Extra library:** [FastNoiseLite](https://github.com/Auburn/FastNoiseLite) bundled as a local JAR

---

## Requirements
- **JDK 23** (check: `java -version`)
- **Maven 3.9+** (check: `mvn -v`)
- Git (if you clone via Git)

---

## Local dependency (FastNoiseLite)

This project uses **FastNoiseLite** as a local Maven dependency. The JAR is already included at:

```
src/libs/FastNoiseLite.jar
```

Install it into your local Maven repository with the same coordinates used in `pom.xml` (`groupId=local`, `artifactId=FastNoiseLite`, `version=1`):

```bash
mvn install:install-file   -Dfile=src/libs/FastNoiseLite.jar   -DgroupId=local   -DartifactId=FastNoiseLite   -Dversion=1   -Dpackaging=jar   -DgeneratePom=true
```

You only need to run this once per machine (or when you clean your local Maven repo).

---

## Build

```bash
mvn clean package
```

Artifacts will be created under `target/`.

---

## Run

### Run classes directly
```bash
# Compile first
mvn -q compile

# Then run the main class from target/classes
java -cp target/classes cz.cvut.game.terroria.Main
```

---

## Project structure

```
terroria/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ cz/cvut/game/terroria/Main.java     # entry point
│  │  └─ resources/
│  └─ test/                                      # JUnit 5 tests 
├─ src/libs/FastNoiseLite.jar                    # local dependency
├─ pom.xml
└─ README.md
```

---

## Origin & attribution

This personal learning project was **inspired by and based on** the public tutorial repository:  
**PlatformerTutorial** by *KaarinGaming* — https://github.com/KaarinGaming/PlatformerTutorial .  
The structure and several ideas were adapted and extended for educational purposes. Any mistakes or deviations are my own.


---

## Notes
- `pom.xml` sets **Java 23** via:
  ```xml
  <maven.compiler.source>23</maven.compiler.source>
  <maven.compiler.target>23</maven.compiler.target>
  ```
- FastNoiseLite is referenced as:
  ```xml
  <dependency>
    <groupId>local</groupId>
    <artifactId>FastNoiseLite</artifactId>
    <version>1</version>
    <scope>compile</scope>
  </dependency>
  ```

---


## Author
**Gleb Sagaidak**  
GitHub: [@Gleb-Sagaidak](https://github.com/Gleb-Sagaidak)

---

## Step-by-step: configure & run (copy/paste)

```bash
# 0) Go to project root
cd terroria

# 1) Install local dependency (FastNoiseLite) into local Maven repo
mvn install:install-file   -Dfile=src/libs/FastNoiseLite.jar   -DgroupId=local   -DartifactId=FastNoiseLite   -Dversion=1   -Dpackaging=jar   -DgeneratePom=true

# 2) Build
mvn clean package

# 3) Run (using Maven Exec)
mvn -q org.codehaus.mojo:exec-maven-plugin:3.1.0:java   -Dexec.mainClass=cz.cvut.game.terroria.Main
```
