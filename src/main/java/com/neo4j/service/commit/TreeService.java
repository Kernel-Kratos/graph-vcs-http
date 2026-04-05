package com.neo4j.service.commit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.neo4j.node.FileBlob;
import com.neo4j.node.Tree;

@Service
public class TreeService {

    public Tree treeRepresentation (List<String> filePaths, MultipartFile file){

        Tree rootTree = new Tree();
        rootTree.setFolderName("/");
        rootTree.setSubTrees(new ArrayList<>());
        rootTree.setFileBlob(new ArrayList<>());

        for (String filePath : filePaths) {
            Tree currentNode = rootTree;
            String[] directoryNodes = filePath.split("/");
            for (int i = 0; i < directoryNodes.length - 1; i++) {
                //rewrite this using guard clause(later)
                String temp = directoryNodes[i]; //to trick the compiler into thinking this is the final variable.
                // this is becuase whenever a lamda is used in java it is neccessary for any outside variable to be final and java can't guarentee
                // it being final becuase i keeps on changing. 
                Tree matchingTree = currentNode.getSubTrees().stream()
                        .filter(tree -> tree.getFolderName().equals(temp))
                        .findFirst() //this is to stop it from streaming the whole list 
                        .orElse(null);
                if (matchingTree == null){
                    Tree newDirectory = new Tree();
                    newDirectory.setFolderName(directoryNodes[i]);
                    newDirectory.setSubTrees(new ArrayList<>());
                    newDirectory.setFileBlob(new ArrayList<>());
                    currentNode.getSubTrees().add(newDirectory);
                    currentNode = newDirectory;
                }
                else{
                currentNode = matchingTree;
                }
            }
            FileBlob matchingBlob = currentNode.getFileBlob().stream()
                    .filter(fileBlob -> fileBlob.getFileName().equals(directoryNodes[directoryNodes.length - 1]))
                    .findFirst()
                    .orElse(null);
            if (matchingBlob == null){
                FileBlob newBlob = new FileBlob();
                newBlob.setFileName(directoryNodes[directoryNodes.length - 1]);
                try {
                    newBlob.setRawContent(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Error", e);
                }
                currentNode.getFileBlob().add(newBlob);
            }
            else{
                try{
                    byte[] fileContent = file.getBytes();
                    if (!Arrays.equals(fileContent, matchingBlob.getRawContent())) {
                        matchingBlob.setRawContent(fileContent);
                }
                } catch(IOException e){
                    throw new RuntimeException("Error", e);
                }
            }
            
        }
        return rootTree;
    }
    // why is rootTree = currentNode not done before returning rootTree?
    /*when I did currentNode = rootTree,
    i made the pointer currentNode point to where rootTree was pointing.
    then i initialized the subtrees and fileBlob to arrayList this means, rootTree was also initialized
    similarly when I added subtrees and fileBlobs to list, rootTree was also updated and there is no need to save it again.
    Only difference is rootTree points to / and currentNode will point to whatever the lastest addition was  */
}
