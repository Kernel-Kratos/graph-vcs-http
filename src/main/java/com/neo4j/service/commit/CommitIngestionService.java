package com.neo4j.service.commit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.dto.CommitDto;

import com.neo4j.node.Commit;
import com.neo4j.node.Depository;
import com.neo4j.node.Tree;

import com.neo4j.repository.CommitRepository;
import com.neo4j.repository.DepositoryRepository;
import com.neo4j.utils.HashUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommitIngestionService {

    private final TreeService treeService;
    private final CommitRepository commitRepository;
    private final DepositoryRepository depositoryRepository;

    public Commit commitSave (List<String> filepaths, List<MultipartFile> files, 
        String author, String email, String message, String repoName) {
            Tree masterTree = treeService.treeRepresentation(filepaths, files);
            Commit newCommit = new Commit();
            newCommit.setAuthor(author);
            newCommit.setEmail(email);
            newCommit.setMessage(message);
            newCommit.setTimestamp(LocalDateTime.now());
            newCommit.setTree(masterTree);
            Depository depository;
            String parentHash;
            try { //this might bite me in the ass later on
                depository = depositoryRepository.findById(repoName)
                    .orElseThrow(() -> new RuntimeException());
                newCommit.setParent(depository.getHead());
                parentHash = newCommit.getParent().getHashId();
            } catch (RuntimeException e) {
                newCommit.setParent(null);
                parentHash = null;
            }
       
        String rawData = newCommit.getEmail() + newCommit.getAuthor() + newCommit.getMessage() 
                + newCommit.getTimestamp().toString() + newCommit.getTree().getHashId() + (parentHash != null ? parentHash: ""); //the last part "" appends the string with nothing. 
        newCommit.setHashId(HashUtil.hashString(rawData));
        return commitRepository.save(newCommit);
    }
    
    public CommitDto convertToCommitDto (Commit commit) {
        CommitDto commitDto = new CommitDto();
        commitDto.setCommitHash(commit.getHashId());
        commitDto.setTreeHash(commit.getTree().getHashId());
        return commitDto;
    }
    
}
