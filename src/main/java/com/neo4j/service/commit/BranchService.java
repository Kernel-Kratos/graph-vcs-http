package com.neo4j.service.commit;

import org.springframework.stereotype.Service;

import com.neo4j.node.Branch;
import com.neo4j.node.Commit;
import com.neo4j.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BranchService {
    private final BranchRepository branchRepository;
    public Branch addToBranch (String branchName, Commit commit) {
        Branch getBranch = branchRepository.findById(branchName)
                .orElseGet(() -> {
                    Branch newBranch = new Branch();
                    newBranch.setBranchName(branchName);
                    return branchRepository.save(newBranch);
                });
        getBranch.setCommit(commit);
        return branchRepository.save(getBranch);
    }
}
