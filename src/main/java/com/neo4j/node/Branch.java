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

    @Id 
    private String branchId; //sincd branch names are unique but neo4j puts them in global space so if 2 diff repos have same branch 
    // the branch is reused instead of creating a new one
    private String branchName;

    //in git branch points to the lastest commit.
    // from there we can follow the trail to upto the first commit.
    //Branch only points to the latest commit on it and commit have no clue which branch they belong to
    // and relation name is has target because 
    @Relationship(type = "POINTS_TO", direction = Relationship.Direction.OUTGOING) 
    private Commit commit;
    
    public Branch(String branchId){
        this.branchId = branchId;
    }
}
