package com.neo4j.service.commit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.dto.CommitDto;
import com.neo4j.node.Commit;
import com.neo4j.node.Tree;
import com.neo4j.repository.CommitRepository;
import com.neo4j.utils.HashUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommitIngestionService {

    private final TreeService treeService;
    private final CommitRepository commitRepository;

    public Commit commitSave (List<String> filepaths, List<MultipartFile> files, 
        String author, String email, String message, String parentHash) {
            Tree masterTree = treeService.treeRepresentation(filepaths, files);
            Commit newCommit = new Commit();
            newCommit.setAuthor(author);
            newCommit.setEmail(email);
            newCommit.setMessage(message);
            newCommit.setTimestamp(LocalDateTime.now());
            newCommit.setTree(masterTree);
            if(parentHash == null){
                newCommit.setParent(null);
            }
            else{
                Commit parent = commitRepository.findById(parentHash)
                        .orElseThrow(() -> new RuntimeException("no parent hash found with this id"));
                newCommit.setParent(parent);
            }
        String rawData = newCommit.getEmail() + newCommit.getAuthor() + newCommit.getMessage() 
                + newCommit.getTimestamp().toString() + newCommit.getTree().getHashId() + (parentHash != null ? parentHash: ""); //wtf is this last one
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
