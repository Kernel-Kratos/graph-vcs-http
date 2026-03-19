package com.neo4j.node;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Node
public class FileBlob{
    @Id
    private String hashId;
    private String fileName;
    //using byte[] instead of String because it will keep the structure as it is and also because i can store anything.
    private byte[] rawContent;

}