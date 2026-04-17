package com.neo4j.node;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  
@RelationshipProperties
public class BlobPointer {

    @RelationshipId
    private Long id;
    
    private String fileName;

    @TargetNode
    private FileBlob blob;

}

/* Reason :- This has to be done as :-
When User 1 uploads desktop/src/hello.txt
and User 2 uploads desktop/src/hello2.txt and
they both have same contents the hash will be same and sdn(spring data neo4j) will draw arrow for user2 to user1's hello.txt but in 
this process user2's file name is lost
To combat this i'll be moving the name from the nodes to the arrows */
