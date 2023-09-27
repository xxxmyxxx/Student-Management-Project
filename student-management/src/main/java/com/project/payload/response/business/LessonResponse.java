package com.project.payload.response.business;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonResponse {

    private Long lessonId;
    private String lessonName;
    private int creditScore;
    private boolean isCompulsory;
}