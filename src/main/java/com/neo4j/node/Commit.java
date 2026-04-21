package com.neo4j.node;


import java.time.LocalDateTime;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node
public class Commit {
    //replaced spring's id with neo4j's id
    @Id
    private String hashId;
    private String author;
    private String email;
    private String message;
    private LocalDateTime timestamp;

    @Relationship(type = "HAS_PARENT", direction = Relationship.Direction.OUTGOING)
    private Commit parent;

    @Relationship(type = "HAS_FOLDERS", direction = Relationship.Direction.OUTGOING)
    private Tree tree;
}
