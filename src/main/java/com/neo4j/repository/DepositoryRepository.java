package com.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.neo4j.node.Depository;

public interface DepositoryRepository extends Neo4jRepository<Depository, String>{
    @Query("MATCH (d:Depository {repoName: $depositoryName}) OPTIONAL MATCH (d)-[oldHead:HAS_HEAD]->() DELETE oldHead WITH d MATCH (c: Commit{hashId : $newHead}) MERGE (d)-[newHead:HAS_HEAD]->(c) RETURN d;")
    void updateBranchHead (String depositoryName, String newHead);

}
