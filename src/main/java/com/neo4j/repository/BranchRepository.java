package com.neo4j.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.neo4j.node.Branch;

public interface BranchRepository extends Neo4jRepository<Branch, String> {
    @Query( "MATCH (b:Branch {branchName: $BranchName}) OPTIONAL MATCH (b)-[oldrel:POINTS_TO]->(c1:Commit) DELETE oldrel WITH b  MATCH (c2: Commit{hashId : $commitHash}) MERGE (b)-[newrel:POINTS_TO]->(c2) RETURN b;")
    Optional<Branch> updateBrachPointer (String BranchName, String commitHash);

}
