package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.neo4j.node.Depository;

public interface DepositoryRepository extends Neo4jRepository<Depository, String>{

}
