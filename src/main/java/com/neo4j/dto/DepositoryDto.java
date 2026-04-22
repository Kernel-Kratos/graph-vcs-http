package com.neo4j.dto;

import lombok.Data;

@Data
public class DepositoryDto {
    private String repoName;
    private String branchName;
    private CommitDto commitDto;
}
