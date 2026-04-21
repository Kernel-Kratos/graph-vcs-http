package com.neo4j.service.commit;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.node.BlobPointer;
import com.neo4j.node.FileBlob;
import com.neo4j.node.Tree;
import com.neo4j.repository.TreeRepository;
import com.neo4j.utils.HashUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TreeService {
    private final TreeRepository treeRepository;
    //implement a feature in controller which throws error if filepaths.length
    public Tree treeRepresentation (List<String> filePaths, List<MultipartFile> file){

        Tree rootTree = new Tree();
        rootTree.setFolderName("/");
        rootTree.setSubTrees(new ArrayList<>());
        rootTree.setBlobPointers(new ArrayList<>());

        for (int i = 0; i < filePaths.size(); i++ ) {
            Tree currentNode = rootTree;
            String[] directoryNodes = filePaths.get(i).split("/");
            for (int j = 0; j < directoryNodes.length - 1; j++) {
                //rewrite this using guard clause(later)
                String temp = directoryNodes[j]; //to trick the compiler into thinking this is the final variable.
                // this is becuase whenever a lamda is used in java it is neccessary for any outside variable to be final and java can't guarentee
                // it being final becuase i keeps on changing. 
                Tree matchingTree = currentNode.getSubTrees().stream()
                        .filter(tree -> tree.getFolderName().equals(temp))
                        .findFirst() //this is to stop it from streaming the whole list 
                        .orElse(null);
                if (matchingTree == null){
                    Tree newDirectory = new Tree();
                    newDirectory.setFolderName(directoryNodes[j]);
                    newDirectory.setSubTrees(new ArrayList<>());
                    newDirectory.setBlobPointers(new ArrayList<>());
                    currentNode.getSubTrees().add(newDirectory);
                    currentNode = newDirectory;
                }
                else{
                currentNode = matchingTree;
                }
            }
            BlobPointer matchingBlobName = currentNode.getBlobPointers().stream()
                    .filter(blobPointer -> blobPointer.getFileName().equals(directoryNodes[directoryNodes.length - 1]))
                    .findFirst()
                    .orElse(null);
            if (matchingBlobName == null){
                BlobPointer newBlobName = new BlobPointer();
                newBlobName.setFileName(directoryNodes[directoryNodes.length - 1]);
                try {
                    FileBlob newBlob = new FileBlob();
                    newBlob.setRawContent(file.get(i).getBytes());
                    newBlobName.setBlob(newBlob);
                } catch (IOException e) {
                    throw new RuntimeException("Error", e);
                }
                currentNode.getBlobPointers().add(newBlobName);
            }
            else{
                try{
                    byte[] fileContent = file.get(i).getBytes();
                    if (!Arrays.equals(fileContent, matchingBlobName.getBlob().getRawContent())) {
                    FileBlob newFileBlob = new FileBlob();
                    newFileBlob.setRawContent(fileContent);
                    matchingBlobName.setBlob(newFileBlob);
                }
                } catch(IOException e){
                    throw new RuntimeException("Error", e);
                }
            }
        }
        rootTree = hashingIt(rootTree);
        treeRepository.save(rootTree);
        return rootTree;
    }
    // why is rootTree = currentNode not done before returning rootTree?
    /*when I did currentNode = rootTree,
    i made the pointer currentNode point to where rootTree was pointing.
    then i initialized the subtrees and fileBlob to arrayList this means, rootTree was also initialized
    similarly when I added subtrees and fileBlobs to list, rootTree was also updated and there is no need to save it again.
    Only difference is rootTree points to / and currentNode will point to whatever the lastest addition was  */


    public static Tree hashingIt (Tree tree) {
        for (Tree currentNode : tree.getSubTrees()) {
            hashingIt(currentNode);
        }
        for (BlobPointer blobPointer : tree.getBlobPointers()){
                try{
                blobPointer.getBlob().setHashId(HashUtil.hashBlob(blobPointer.getBlob().getRawContent()));
                } catch (NoSuchAlgorithmException e){
                    throw new RuntimeException(e);
                }
        }
        
        // In Merkle DAG, parent node is generated acc to the hashes of its children.
        // Below i made a new ArrayList childhashes which will store the hashes of "current children of node"
        //eg :- java/hello/hell/hi.java
        //we are on hello then this list will store : hash of hi.java, hell and the generate a hash based on this.
        List<String> childHashes = new ArrayList<>();

        for (BlobPointer blobpPointer : tree.getBlobPointers()){
            childHashes.add("blob:" + blobpPointer.getBlob().getHashId());
        }

        for (Tree subTree : tree.getSubTrees()){
            childHashes.add("tree:" + subTree.getHashId());
        }

        Collections.sort(childHashes);

        String commbinedHahses = String.join("", childHashes);
        tree.setHashId(HashUtil.hashString(commbinedHahses));
        return tree;
    }

    // added "tree" & "blob" to avoid collison and re-wrtiing.
    // because if a file's hash ends up being 08732... 
    // and the folder's hash ends uo being same 0832.. this will lead to collison and possibly rewrite.
}

