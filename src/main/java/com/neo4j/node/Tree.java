package com.neo4j.node;

import java.util.List;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node
public class Tree {
    @Id
    private String hashId;
    private String folderName;

    @Relationship(type = "HAS_SUBTREES", direction = Relationship.Direction.OUTGOING)
    private List<Tree> subTrees;

    @Relationship(type = "CONTAINS_BLOB", direction = Relationship.Direction.OUTGOING)
    private List<BlobPointer> blobPointers;
}
 