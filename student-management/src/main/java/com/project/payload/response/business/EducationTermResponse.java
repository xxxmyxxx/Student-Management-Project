package com.project.payload.response.business;

import com.project.entity.enums.Term;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EducationTermResponse {

    private Long id;
    private Term term;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastRegistrationDate;
}