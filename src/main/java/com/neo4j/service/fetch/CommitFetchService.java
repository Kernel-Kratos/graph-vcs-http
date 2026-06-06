package com.neo4j.service.fetch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public Path fetchAllCommit (String repoName, String branchName) {
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
           do {
                createRepoContentOnDisk(commit.getTree(), repoPath);
                commit = commit.getParent();
           } while (commit != null);
        } catch (FileAlreadyExistsException e) {

        } catch (IOException e) {
            System.err.println(e);
            System.out.println(repoPath.toString());
        }
        String zipName = repoName + "-" + branchName + ".zip";
        Path zipPath = Path.of(createBin + "/" + zipName);
        try {
            Files.deleteIfExists(zipPath);
            Files.createFile(zipPath);
            zipPath = Path.of(createBin + "/" + zipName);
            zipit(repoPath, zipPath);
            deleteRepoFromDisk(repoPath);
            return zipPath;
        } catch (IOException e) { 
            System.out.println(e);
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

    private void zipit(Path repoPath, Path zipPath) throws FileNotFoundException, IOException  {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(repoPath, new SimpleFileVisitor<Path>(){
        @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(repoPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }
    
    private void deleteRepoFromDisk (Path repoPath) throws IOException {
        Files.walkFileTree(repoPath, new SimpleFileVisitor<Path>(){
        @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;   
            }
        @Override
            public FileVisitResult postVisitDirectory (Path file, IOException exc) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        }); 
    }
}


    

