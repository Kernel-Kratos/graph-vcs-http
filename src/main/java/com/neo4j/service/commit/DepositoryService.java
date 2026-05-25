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
                    newDepository.setBranches(new ArrayList<>());
                    return depositoryRepository.save(newDepository);
                });
       Branch updatedBranch = branchService.addToBranch(branchName, repoName, commit);
       boolean branchExists = depository.getBranches().stream()
                    .anyMatch(branch -> branch.getBranchName().equals(branchName));
        if (!branchExists){
            depository.getBranches().add(updatedBranch);
            depositoryRepository.save(depository);
            depositoryRepository.updateBranchHead(depository.getRepoName(), commit.getHashId());
            return depository;
        }
        else{
            depository.getBranches().removeIf(branchname -> (branchName.equals(updatedBranch.getBranchName())));
            depository.getBranches().add(updatedBranch);
            depositoryRepository.save(depository);
            depositoryRepository.updateBranchHead(depository.getRepoName(), commit.getHashId());
            return depository;
        }   
    }

    public DepositoryDto convertTDepositoryDto (Depository depository, CommitDto commitDto, String branchName){
        DepositoryDto depositoryDto = new DepositoryDto();
        depositoryDto.setRepoName(depository.getRepoName());
        depositoryDto.setBranchName(branchName); //reason i'm fetching this from controller instead of db is because db won't guarentee order until explicity asked
        depositoryDto.setCommitDto(commitDto);
        return depositoryDto;
    }
}
