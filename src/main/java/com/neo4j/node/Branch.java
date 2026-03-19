package com.neo4j.node;


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
public class Branch {

    @Id //sincd branch names are unique
    private String branchName;

    //in git branch points to the lastest commit.
    // from there we can follow the trail to upto the first commit.
    //Branch only points to the latest commit on it and commit have no clue which branch they belong to
    // and relation name is has target because 
    @Relationship(type = "POINTS_TO", direction = Relationship.Direction.OUTGOING) 
    private Commit commit;
    
}
