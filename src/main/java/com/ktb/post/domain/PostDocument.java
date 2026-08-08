package com.ktb.post.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;


import java.time.LocalDateTime;

@Getter
@Setting(settingPath = "elasticsearch/post-setting.json")
@Mapping(mappingPath = "elasticsearch/post-mapping.json")
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
