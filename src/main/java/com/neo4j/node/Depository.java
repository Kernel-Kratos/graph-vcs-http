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
public class Depository {
    @Id
    private String repoName;

    @Relationship(type = "HAS_BRANCHES", direction = Relationship.Direction.OUTGOING)
    private List<Branch> branches;

    @Relationship(type = "HAS_HEAD", direction = Relationship.Direction.OUTGOING)
    private Commit head;
}
