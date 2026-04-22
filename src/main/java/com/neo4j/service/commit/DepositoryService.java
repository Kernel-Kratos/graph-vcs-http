package com.neo4j.service.commit;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.neo4j.dto.CommitDto;
import com.neo4j.dto.DepositoryDto;
import com.neo4j.node.Branch;
import com.neo4j.node.Commit;
import com.neo4j.node.Depository;
import com.neo4j.repository.DepositoryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DepositoryService {
    private final DepositoryRepository depositoryRepository;
    private final BranchService branchService;

    public Depository addToDepository (String repoName, String branchName, Commit commit) {
        Depository depository = depositoryRepository.findById(repoName)
                .orElseGet(() -> {
                    Depository newDepository = new Depository();
                    newDepository.setRepoName(repoName);
                    newDepository.setBranch(new ArrayList<>());
                    return depositoryRepository.save(newDepository);
                });
       Branch updatedBranch = branchService.addToBranch(branchName, commit);
       boolean branchExists = depository.getBranch().stream()
                    .anyMatch(branch -> branch.getBranchName().equals(branchName));
        if (!branchExists){
            depository.getBranch().add(updatedBranch);
        }        
        depository.setHead(commit);
        return depositoryRepository.save(depository);
    }

    public DepositoryDto convertTDepositoryDto (Depository depository, CommitDto commitDto){
        DepositoryDto depositoryDto = new DepositoryDto();
        depositoryDto.setRepoName(depository.getRepoName());
        depositoryDto.setBranchName(depository.getBranch().getLast().getBranchName());
        depositoryDto.setCommitDto(commitDto);
        return depositoryDto;
    }
}


/*
    
*/