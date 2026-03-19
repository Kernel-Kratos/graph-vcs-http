package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.neo4j.node.Commit;

public interface CommitRepository extends Neo4jRepository<Commit, String> {

}
