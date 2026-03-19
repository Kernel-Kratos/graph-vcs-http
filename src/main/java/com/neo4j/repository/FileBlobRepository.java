package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.neo4j.node.FileBlob;

public interface FileBlobRepository extends Neo4jRepository<FileBlob, String>{

}
