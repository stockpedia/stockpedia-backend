package com.ktb.post.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;


import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(indexName = "posts")
public class PostDocument {

    @Id
    private String id;

    private Integer postId;

    private String title;

    private String content;

    private String memberNickname;

    private LocalDateTime createdAt;
}
