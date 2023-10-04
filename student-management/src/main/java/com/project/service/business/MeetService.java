package com.project.service.business;

import com.project.entity.business.Meet;
import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.MeetMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.business.MeetResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.MeetRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.user.UserService;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MethodHelper methodHelper;
    private final DateTimeValidator dateTimeValidator;
    private final UserService userService;
    private final MeetMapper meetMapper;
    private final PageableHelper pageableHelper;

    // Not : Save() ************************
    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest, MeetRequest meetRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        User advisoryTeacher = methodHelper.isUserExistByUsername(username);
        // !!! advisor Teacher mi kontrolu
        methodHelper.checkAdvisor(advisoryTeacher);
        // !!! requestten gelen meet saatleri dogru mu
        dateTimeValidator.checkTimeWithException(meetRequest.getStartTime(), meetRequest.getStopTime());
        // !!! Teacher in eski meeetleri ile cakisma var mi
        checkMeetConflict(advisoryTeacher.getId(),
                meetRequest.getDate(),
                meetRequest.getStartTime(),
                meetRequest.getStopTime());


        // !!! Student icin cakisma var mi
        for (Long studentId: meetRequest.getStudentIds()) {
            User student = methodHelper.isUserExist(studentId);
            methodHelper.checkRole(student, RoleType.STUDENT);
            checkMeetConflict(studentId,
                    meetRequest.getDate(),
                    meetRequest.getStartTime(),
                    meetRequest.getStopTime());
        }
        // !!! Meet e katilacak ogrencileri getirdim
        List<User> students = userService.getStudentById(meetRequest.getStudentIds());
        // !!! DTO --> POJO
        Meet meet = meetMapper.mapMeetRequestToMeet(meetRequest);
        meet.setStudentList(students);
        meet.setAdvisoryTeacher(advisoryTeacher);

        Meet savedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_SAVE)
                .object(meetMapper.mapMeetToMeetResponse(savedMeet))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    private void checkMeetConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime stopTime){
        List<Meet> meets ;

        // mevcut Meetler getiriliyor
        if(Boolean.TRUE.equals(methodHelper.isUserExist(userId).getIsAdvisor())) {
            meets = meetRepository.getByAdvisoryTeacher_IdEquals(userId);
        } else meets = meetRepository.getByStudentList_IdEquals(userId);

        // !!! cakisma kontrolu
        for(Meet meet: meets){
            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime  = meet.getEndTime();

            if(meet.getDate().equals(date) &&
                    (
                            (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                                    (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                                    (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                                    (startTime.equals(existingStartTime)) ||
                                    (stopTime.equals(existingStopTime))
                    )
            ){
                throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
            }
        }


    }

    // Not: delete() ***********************
    public ResponseMessage delete(Long meetId, HttpServletRequest httpServletRequest) {
        Meet meet = isMeetExistById(meetId);
        //!!! Teacher ise sadece kendi Meet lerini silebilsin
        isTeacherControl(meet, httpServletRequest);

        meetRepository.deleteById(meetId);
        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    private Meet isMeetExistById(Long meetId){
        return  meetRepository.findById(meetId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE,meetId)));
    }

    // Not: Update() ************************
    public ResponseMessage<MeetResponse> updateMeet(MeetRequest updateMeetRequest, Long meetId,
                                                    HttpServletRequest httpServletRequest) {
        Meet meet = isMeetExistById(meetId);
        //!!! Teacher ise sadece kendi Meet lerini guncelliyebilsin..
        isTeacherControl(meet, httpServletRequest);
        dateTimeValidator.checkTimeWithException(updateMeetRequest.getStartTime(), updateMeetRequest.getStopTime());
        if(        !( meet.getDate().equals(updateMeetRequest.getDate()) &&
                meet.getStartTime().equals(updateMeetRequest.getStartTime()) &&
                meet.getEndTime().equals(updateMeetRequest.getStopTime())
        )
        ) {
            // !!! Student icin cakisma var mi
            for (Long studentId: updateMeetRequest.getStudentIds()){
                checkMeetConflict(studentId,
                        updateMeetRequest.getDate(),
                        updateMeetRequest.getStartTime(),
                        updateMeetRequest.getStopTime());
            }

            // !!! Teacher icin cakisma var mi
            checkMeetConflict(meet.getAdvisoryTeacher().getId(),
                    updateMeetRequest.getDate(),
                    updateMeetRequest.getStartTime(),
                    updateMeetRequest.getStopTime());
        }

        List<User> students = userService.getStudentById(updateMeetRequest.getStudentIds());
        Meet updatedMeet = meetMapper.mapMeetUpdateRequestToMeet(updateMeetRequest, meetId);
        updatedMeet.setStudentList(students);
        updatedMeet.setAdvisoryTeacher(meet.getAdvisoryTeacher());

        Meet savedMeet = meetRepository.save(updatedMeet);

        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(meetMapper.mapMeetToMeetResponse(savedMeet))
                .build();
    }

    // Not: gettAllByAdvTeacherByPage() **********************************************
    public ResponseEntity<Page<MeetResponse>>getAllByAdvTeacherByPage(HttpServletRequest httpServletRequest,
                                                                      int page,
                                                                      int size){
        String userName = (String) httpServletRequest.getAttribute("username");
        User advisoryTeacher = methodHelper.isUserExistByUsername(userName);
        methodHelper.checkAdvisor(advisoryTeacher);

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
        return ResponseEntity.ok(meetRepository.findByAdvisoryTeacher_IdEquals(advisoryTeacher.getId(), pageable)
                .map(meetMapper::mapMeetToMeetResponse));
    }

    // Not: getAllMeetByStudent() ***********
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getAttribute("username");
        User student = methodHelper.isUserExistByUsername(username);
        methodHelper.checkRole(student,RoleType.STUDENT);

        return  meetRepository.getByStudentList_IdEquals(student.getId())
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());

    }

    // Not: getAll ************************************
    public List<MeetResponse>getAll(){
        return meetRepository.findAll()
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());
    }

    // Not: getAllWithPage ****************************
    public Page<MeetResponse> getAllMeetByPage(int page, int size){
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size);
        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
    }

    // Not: gettAllByAdvTeacherUsername() *****************************************
    public ResponseEntity getAllMeetByTeacher(HttpServletRequest httpServletRequest){
        String userName = (String) httpServletRequest.getAttribute("username");
        User advisoryTeacher = methodHelper.isUserExistByUsername(userName);
        methodHelper.checkAdvisor(advisoryTeacher);

        List<MeetResponse> meetResponseList =
                meetRepository.getByAdvisoryTeacher_IdEquals(advisoryTeacher.getId())
                        .stream()
                        .map(meetMapper::mapMeetToMeetResponse).collect(Collectors.toList());
        return ResponseEntity.ok(meetResponseList);
    }

    private void isTeacherControl(Meet meet, HttpServletRequest httpServletRequest){
        //!!! Teacher ise sadece kendi Meet lerini silebilsin
        String userName = (String) httpServletRequest.getAttribute("username");
        User teacher = methodHelper.isUserExistByUsername(userName);
        if(
                (teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)) && // metodu tetikleyenin Role bilgisi TEACHER ise
                        !(meet.getAdvisoryTeacher().getId().equals(teacher.getId())) // Teacher, baskasinin Meet ini silmeye calisiyorsa
        )
        {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }
}
