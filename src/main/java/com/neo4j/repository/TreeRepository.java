package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.neo4j.node.Tree;

public interface TreeRepository extends Neo4jRepository<Tree, String> {

}
