package com.project.entity.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lessonName;

    private Integer creditScore;

    private Boolean isCompulsory;

    @JsonIgnore
    @ManyToMany(mappedBy = "lessons", cascade = CascadeType.REMOVE)
    private Set<LessonProgram> lessonPrograms;
}