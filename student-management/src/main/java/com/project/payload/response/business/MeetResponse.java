package com.project.payload.response.business;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.entity.user.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetResponse {

    private Long id;
    private String description;
    private LocalTime startTime;
    private LocalTime stopTime;
    private LocalDate date;
    private Long advisorTeacherId;
    private String teacherName;
    private String teacherSsn;
    private String username;
    private List<User> students;
}