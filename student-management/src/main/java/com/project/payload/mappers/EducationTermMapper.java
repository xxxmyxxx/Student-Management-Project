package com.project.payload.mappers;

import com.project.entity.business.EducationTerm;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.business.EducationTermResponse;
import org.springframework.stereotype.Component;

@Component
public class EducationTermMapper {

    public EducationTerm mapEducationTermRequestToEducationTerm(EducationTermRequest educationTermRequest) {
        return EducationTerm.builder()
                .term(educationTermRequest.getTerm())
                .startDate(educationTermRequest.getStartDate())
                .endDate(educationTermRequest.getEndDate())
                .lastRegistrationDate(educationTermRequest.getLastRegistrationDate())
                .build();
    }
    public EducationTermResponse mapEducationTermToEducationTermResponse(EducationTerm educationTerm){
        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }
    public EducationTerm mapEducationTermRequestToUpdatedEducationTerm(Long id, EducationTermRequest educationTermRequest){
        return mapEducationTermRequestToEducationTerm(educationTermRequest)
                .toBuilder()
                .id(id)
                .build();
    }
}