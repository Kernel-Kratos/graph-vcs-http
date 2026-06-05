package com.neo4j.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.nio.file.Path;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neo4j.service.fetch.CommitFetchService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("graph-vcs-http/api/v1")
@RestController
public class CommitFetchController {
    private final CommitFetchService commitFetchService;

    @GetMapping("{depositoryName}/{branchName}")
    public ResponseEntity<Resource> getAllCommits (@PathVariable String depositoryName, @PathVariable String branchName) {
        Path zipPath = commitFetchService.fetchAllCommit(depositoryName, branchName);
        File file = new File(zipPath.toString()); // to create a reference to the zip-file in memory.
        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(file.length());

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
        return new ResponseEntity<>(resource,headers, HttpStatus.valueOf(200));
    }

}
