package com.neo4j.service.fetch;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.neo4j.node.BlobPointer;
import com.neo4j.node.Branch;
import com.neo4j.node.Commit;
import com.neo4j.node.Tree;
import com.neo4j.repository.BranchRepository;
import com.neo4j.repository.CommitRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommitFetchService {
    private final CommitRepository commitRepository;
    private final BranchRepository branchRepository;

    Set<String> blobList = new HashSet<>();

    public Commit findCommit (String hashId) {
        return commitRepository.findById(hashId)
                .orElseThrow(() -> new RuntimeException("Commit Not found"));
    } 
    private void createRepoContentOnDisk (Tree masterTree, Path dirPath){
        for (Tree tree : masterTree.getSubTrees()) {
            String dir = tree.getFolderName();
            Path currentPath = Path.of(dirPath + "/" + dir);
            try {
                Files.createDirectory(currentPath);
            } catch (FileAlreadyExistsException e) {
                System.err.println(e);
                //todo
            }
            catch (IOException e) {
                System.out.println("Oops!!! IO exception occured when trying create the dir");
                System.out.println(e);
            }
            createRepoContentOnDisk(tree, currentPath);
            if (!tree.getBlobPointers().isEmpty()) {
                for (BlobPointer blobPointer : tree.getBlobPointers()) {
                    Path filePath = Path.of(currentPath + "/" + blobPointer.getFileName());
                    try {
                        Files.createFile(filePath);
                        Files.write(filePath, blobPointer.getBlob().getRawContent());
                        blobList.add(blobPointer.getFileName());
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
            }
        }
    }
}


//work on all exceptions in createDataondiks. as there no logic what to do when the path exists;
///add next steps