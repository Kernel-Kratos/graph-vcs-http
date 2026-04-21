package com.neo4j.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommitDto {
    private String commitHash;
    private String treeHash;

}
