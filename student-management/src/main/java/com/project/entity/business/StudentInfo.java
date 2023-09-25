package com.project.entity.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.entity.enums.Note;
import com.project.entity.user.User;
import lombok.*;

import javax.persistence.*;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfo { //many
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer absentee;

    private Double midtermExam;

    private Double finalExam;

    private Double examAverage;

    private String infoNote;

    private Note letterGrade;

    @ManyToOne
    @JsonIgnore // request userdan geliyorsa buraya yazacaz
    private User teacher;

    @ManyToOne
    @JsonIgnore
    private User student;

    @ManyToOne
    private Lesson lesson;

    @OneToOne
    private EducationTerm educationTerm;



}
