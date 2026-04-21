# Graph-VCS-Http 

> A cryptographically Content-Addressable Storage (CAS) version control engine built with Spring Boot and Neo4j.

# Introduction
This project explores the internal mechanics of distributed version control systems (like Git) and massive archival networks. Instead of storing flat files, this engine ingests repositories and maps them into a highly efficient, immutable Merkle Directed Acyclic Graph (DAG) in a graph database.

## Core Architecture & Features
* **Recursive Merkle Hashing:** Directories (Trees) and files (Blobs) are hashed bottom-up using SHA-256. A parent folder's state is entirely dependent on the cryptographic hashes of its children.
* **Byte-Level Deduplication:** Identical files or unchanged directories across different commits (or entirely different repositories) are never duplicated. 
* **Graph-Native Pointers:** Filenames are decoupled from the raw byte data. By storing the filename on the relationship edge (`BlobPointer`) rather than the node (`FileBlob`), multiple different filenames across the system can securely point to the exact same raw byte array.
* **Immutable Commit History:** Commits are permanently locked into the DAG, forming an unbreakable, cryptographically verifiable chain of repository states.

## Tech Stack
* **JAVA 21**
* **Spring Boot 3.x**
* **Spring Data Neo4j (SDN)**
* **Neo4j** (Graph Database) 

## The Data Model (Neo4j)

The engine maps standard filesystem concepts into the following Graph Nodes and Relationships:
* `(Commit)` - Represents a snapshot in time. Points to a root `Tree` and a parent `Commit`.
* `(Tree)` - Represents a directory. Contains relationships to sub-trees and files.
* `[CONTAINS_BLOB]` - A rich relationship edge holding the specific `fileName` string.
* `(FileBlob)` - An immutable, name-agnostic node containing only the raw `byte[]` content and its SHA-256 hash.

## Getting Started

### Prerequisites
* Java 21+
* Maven
* A running Neo4j instance (defaulting to `bolt://localhost:7687`)

### Installation & Execution
1. Clone the repository:
   ``` bash
   git clone https://github.com/kernel-kratos/graph-vcs-http.git
   ```
2. Configure your Neo4j credentials in src/main/resources/application.properties
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```




## Current Status & Roadmap
* [x] Core DAG generation and hashing engine.
* [x] Byte-level deduplication and relationship mapping.
* [x] Commit snapshot ingestion.
* [ ] Depository and Branch wrapper logic for repository tracking.
* [ ] Retrieval API to reconstruct filesystem trees from a target commit.
