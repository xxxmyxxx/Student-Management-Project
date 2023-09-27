package com.project.payload.request.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.enums.Day;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgramRequest {

    @NotNull(message = "Please enter a day")
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    @NotNull(message="Please enter start time")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    @NotNull(message="Please enter stop time")
    private LocalTime stopTime;

    @NotNull(message="Please select lesson")
    @Size(min = 1, message = "Lesson must not be empty")
    private Set<Long> lessonIdList;

    @NotNull(message="Please select education term")
    private Long educationTermId;

}