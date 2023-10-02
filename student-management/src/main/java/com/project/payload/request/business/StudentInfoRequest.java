package com.project.payload.request.business;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfoRequest {

    @NotNull(message = "please select education term")
    private Long educationTermId;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @NotNull(message = "please enter midterm exam")
    private Double midtermExam;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    @NotNull(message = "please enter final exam")
    private Double finalExam;

    @NotNull(message = "please enter absentee")
    private Integer absentee;

    @NotNull(message = "please enter info")
    @Size(min=10, max = 200, message = "Info should be at least 10 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Info must consist of the characters .")
    private String infoNote;

    @NotNull(message = "please select lesson")
    private Long lessonId;

    @NotNull(message = "please select student")
    private Long studentId;
}