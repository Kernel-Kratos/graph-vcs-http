package com.neo4j.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.dto.CommitDto;
import com.neo4j.dto.DepositoryDto;
import com.neo4j.node.Commit;
import com.neo4j.node.Depository;
import com.neo4j.service.commit.CommitIngestionService;
import com.neo4j.service.commit.DepositoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@RequestMapping("graph-vcs-http/api/v1")
@RestController
public class CommitIngestionController {
    private final CommitIngestionService commitIngestionService;
    private final DepositoryService depositoryService;

    @PostMapping("/{repoName}/{branchName}")
    public ResponseEntity<DepositoryDto> commitIngestion (@RequestParam List<String> filePath, @RequestParam List <MultipartFile> file,
        @RequestParam String author, @RequestParam String email, @RequestParam String message,
    @PathVariable String repoName, @PathVariable String branchName){
        if (filePath.size() != file.size())
            throw new RuntimeException("There are not equal number of file to corresponding to filepath");
        for (int i = 0; i < filePath.size(); i++){
            if (! filePath.get(i).endsWith(file.get(i).getOriginalFilename())){
                throw new RuntimeException("There is mis-match in the filename in file path & filename of the blob");
            }
        }
        try {
        Commit commit = commitIngestionService.commitSave(filePath, file, author, email, message, repoName);
        CommitDto commitDto = commitIngestionService.convertToCommitDto(commit);
        Depository updatedRepo = depositoryService.addToDepository(repoName, branchName, commit);
        DepositoryDto depositoryDto = depositoryService.convertTDepositoryDto(updatedRepo, commitDto, branchName);    
        return ResponseEntity.ok(depositoryDto);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    

}