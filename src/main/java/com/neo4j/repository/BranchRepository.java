package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.neo4j.node.Branch;

public interface BranchRepository extends Neo4jRepository<Branch, String> {

}
