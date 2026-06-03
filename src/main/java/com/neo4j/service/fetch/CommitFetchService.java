package com.neo4j.service.fetch;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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

    //this method only gets the "lastest" version of a file. cuz it will be only used to downnload the files. 
    public String fetchAllCommit (String repoName, String branchName) {
        Branch branch = branchRepository.findById(repoName + "-" + branchName)
                .orElseThrow(() -> new RuntimeException("branch not found"));
        Commit commit = branch.getCommit();

        String rootdir = "Graph-vcs-https";
        Path rootdirPath = Path.of(System.getProperty("user.dir") + "/" + rootdir);
        try {
            Files.createDirectory(rootdirPath);
        } catch (FileAlreadyExistsException e) {
            //Dir is already present; don't need to do anything
        } catch (IOException e) {
            System.err.println(e);
            System.out.println(rootdirPath.toString());
        }

        Path createBin = Path.of(rootdir + "/bin");
        try {
            Files.createDirectory(createBin);
        } catch (FileAlreadyExistsException e) {
            //Dir is already present; don't need to do anything
        } catch (IOException e) {
            System.err.println(e);
            System.out.println(createBin.toString());
        }   

        Path repoPath = Path.of(createBin + "/" + repoName);
        try {
           Files.createDirectories(repoPath);
           int i = 0; 
           do {
                System.out.println(commit.getTimestamp());
                System.out.println(i);
                createRepoContentOnDisk(commit.getTree(), repoPath);
                i ++;
                commit = commit.getParent();
           } while (commit != null);
        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            System.err.println(e);
            System.out.println(repoPath.toString());
        }
        return null;
    }

    
    private void createRepoContentOnDisk (Tree masterTree, Path dirPath){
        for (Tree tree : masterTree.getSubTrees()) {
            String dir = tree.getFolderName();
            Path currentPath = Path.of(dirPath + "/" + dir);
            try {
                Files.createDirectory(currentPath);
            } catch (FileAlreadyExistsException e) {
                //Dir is already present; don't need to do anything
            }
            catch (IOException e) {
                System.out.println("Oops!!! IO exception occured when trying create the dir");
                System.out.println(e);
                System.out.println(currentPath.toString());
            }
            createRepoContentOnDisk(tree, currentPath);
            if (!tree.getBlobPointers().isEmpty()) {
                for (BlobPointer blobPointer : tree.getBlobPointers()) {
                    Path filePath = Path.of(currentPath + "/" + blobPointer.getFileName());
                    try {
                        Files.createFile(filePath);
                        Files.write(filePath, blobPointer.getBlob().getRawContent());
                        blobList.add(blobPointer.getFileName());
                    } catch(FileAlreadyExistsException e) {
                        //File's latest verison is already present since travesal is done from newewst commit to oldest; don't need to do anything
                    } catch (IOException e) {
                        System.err.println(e);
                        System.out.println(filePath.toString());
                    }
                }
            }
        }
    }
}
