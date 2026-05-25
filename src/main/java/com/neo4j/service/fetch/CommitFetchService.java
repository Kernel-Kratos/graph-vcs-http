package com.neo4j.service.fetch;

import org.springframework.stereotype.Service;

import com.neo4j.node.Commit;
import com.neo4j.repository.CommitRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommitFetchService {
    private final CommitRepository commitRepository;

    public Commit findCommit (String hashId) {
        return commitRepository.findById(hashId)
                .orElseThrow(() -> new RuntimeException("Commit Not found"));
    }
}
