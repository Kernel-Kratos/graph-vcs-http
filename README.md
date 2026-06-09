# Graph-VCS-Http 

> A version control that uses an actual graph database. 

# Introduction
This project explores the internal mechanics of distributed version control systems (like Git) and massive archival networks (like Software Heritage). Instead of storing flat files, this engine ingests repositories and maps them into a highly efficient, immutable Merkle Directed Acyclic Graph (DAG) in a graph database.

> Fun fact: I named this Graph-vcs-http because this is a graph based version control system(vcs) that uses http protocol.

## Core Architecture & Features
* **Recursive Merkle Hashing:** Directories (Trees) and files (Blobs) are hashed bottom-up using SHA-256. A parent folder's state is entirely dependent on the cryptographic hashes of its children.
* **Byte-Level Deduplication:** Identical files or unchanged directories across different commits (or entirely different repositories) are never duplicated. 
* **Graph-Native Pointers:** Filenames are decoupled from the raw byte data. By storing the filename on the relationship edge (`BlobPointer`) rather than the node (`FileBlob`), multiple different filenames across the system can securely point to the exact same raw byte array.
* **Immutable Commit History:** Commits are permanently locked into the DAG, forming an unbreakable, cryptographically verifiable chain of repository states.

## Tech Stack
* **JAVA 21**
* **Spring Boot 4.x**
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

## API Reference
 1. Ingest a commit
    Creates a new commit snapshot from a list of files and constructs underlying Merkle DAG.
    
    **Endpoint: `(Post /graph-vcs-http/api/v1/{repoName}/{branchName})`**
    > Graph-vcs will automatically create the repo or branch if they do-not exist.
    >
    > Use Postman or Insomnia to test; Content Type: multipart/form-data	
    
      | Key | Type | Required | Description |
      |  :---- | ---- |  :---- | ---- |  
      | filepath | List<String> | Yes | The path of the file (eg. src/main/hello.java) |
      | file | List<MultipartFile> | Yes | The actual file(blob) |
      | author | String | Yes | Name of Commit's Author |
      | email | String | Yes | Email of Commit's Author |
      | message | String | Yes | Commit message |

 2.  Download the Repo in Zip File. (more on this later) 
     Downloads the repo in zip file with the `lastest` version of file. 

     **Endpoint: `(Get /graph-vcs-http/api/v1/{repoName}/{branchName})`** 
     > Downloaded zip file will be in the format: repositoryName-branchName.


## Zip File Download
  Whenever server is requested for zip file it will first fetch the files and directory structure from neo4j in memory, then create it on disk. 
  All of these operations happen commit-by-commit. Traversal happens from newset to oldest commit.

  The server will discard a file if a newer version of file has already been written to the disk.
  The server will then convert this directory into a zip file and then stream it to the client.
	
  For the first time, server will `create a graph-vcs-http dir` in project root i.e where `pom.xml` is `located`.
  The graph-vcs-http dir will have a dir named `bin`. This is where all the fetched files and .zip files will be stored.
	

