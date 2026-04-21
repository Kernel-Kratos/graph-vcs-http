package com.neo4j.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.dto.CommitDto;
import com.neo4j.node.Commit;
import com.neo4j.service.commit.CommitIngestionService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@RequestMapping("graph-vcs-https/api/v1")
@RestController
public class CommitIngestionController {
    private final CommitIngestionService commitIngestionService;

    @PostMapping("")
    public ResponseEntity<CommitDto> commitIngestion (@RequestParam List<String> filePath, @RequestParam List <MultipartFile> file,
        @RequestParam String author, @RequestParam String email, @RequestParam String message, @RequestParam(required = false) String parentHash ){
        try {
        Commit commit = commitIngestionService.commitSave(filePath, file, author, email, message, parentHash);
        CommitDto commitDto = commitIngestionService.convertToCommitDto(commit);
        return ResponseEntity.ok(commitDto);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    

}
