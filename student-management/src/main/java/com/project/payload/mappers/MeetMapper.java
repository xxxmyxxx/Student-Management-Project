package com.project.payload.mappers;

import com.project.entity.business.Meet;
import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.business.MeetResponse;
import org.springframework.stereotype.Component;

@Component
public class MeetMapper {

    // !!! DTO --> POJO
    public Meet mapMeetRequestToMeet(MeetRequest meetRequest){
        return Meet.builder()
                .date(meetRequest.getDate())
                .startTime(meetRequest.getStartTime())
                .endTime(meetRequest.getStopTime())
                .description(meetRequest.getDescription())
                .build();
    }

    // !!! POJO --> DTO
    public MeetResponse mapMeetToMeetResponse(Meet meet){
        return MeetResponse.builder()
                .id(meet.getId())
                .date(meet.getDate())
                .startTime(meet.getStartTime())
                .stopTime(meet.getEndTime())
                .description((meet.getDescription()))
                .advisorTeacherId(meet.getAdvisoryTeacher().getId())
                .teacherSsn(meet.getAdvisoryTeacher().getSsn())
                .teacherName(meet.getAdvisoryTeacher().getName())
                .students(meet.getStudentList())
                .build();
    }
    // !!! For Update DTO --> POJO
    public Meet mapMeetUpdateRequestToMeet(MeetRequest meetRequest, Long meetId){
        return Meet.builder()
                .id(meetId)
                .startTime(meetRequest.getStartTime())
                .endTime(meetRequest.getStopTime())
                .date(meetRequest.getDate())
                .description(meetRequest.getDescription())
                .build();
    }

}