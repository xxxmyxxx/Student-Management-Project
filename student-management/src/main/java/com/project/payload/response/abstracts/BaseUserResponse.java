package com.project.payload.response.abstracts;

import com.project.entity.business.LessonProgram;
import com.project.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseUserResponse {

    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String ssn;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;
    private String email;
//    private Set<LessonProgram> lessonPrograms;
//    private Boolean isAdvisorTeacher;
    private String userRole;

}