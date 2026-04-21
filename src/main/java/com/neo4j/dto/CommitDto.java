package com.neo4j.dto;



import lombok.Data;

@Data
public class CommitDto {
    private String commitHash;
    private String treeHash;

}
