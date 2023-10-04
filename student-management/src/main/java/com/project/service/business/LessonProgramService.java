package com.project.service.business;

import com.project.entity.business.EducationTerm;
import com.project.entity.business.Lesson;
import com.project.entity.business.LessonProgram;
import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.LessonProgramMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.LessonProgramRepository;
import com.project.service.helper.PageableHelper;
import com.project.service.user.UserService;
import com.project.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final DateTimeValidator dateTimeValidator;
    private final LessonProgramMapper lessonProgramMapper;
    private final PageableHelper pageableHelper;
    private final UserService userService;



    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdSet){

        Set<LessonProgram> lessonPrograms =  lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdSet);

        if(lessonPrograms.isEmpty()){
            throw  new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_WITHOUT_ID_MESSAGE);
        }

        return lessonPrograms;
    }

    // Not :  Save() ****************************
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
        // !!! lessonProgramda talep edilen dersleri getirelim
        Set<Lesson> lessons = lessonService.getLessonByLessonIdSet(lessonProgramRequest.getLessonIdList());
        // !!! educationTerm getiriliyor
        EducationTerm educationTerm = educationTermService.getEducationTermById(lessonProgramRequest.getEducationTermId());
        // !!! yukarda gelen lessons ici bos mu kontrolu
        if(lessons.isEmpty()){
            throw  new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_IN_LIST);
        }
        // !!!  zaman kontrolu
        dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(), lessonProgramRequest.getStopTime());
        // !!! DTO -- > POJO
        LessonProgram lessonProgram = lessonProgramMapper.mapLessonProgramRequestToLessonProgram(lessonProgramRequest,lessons,educationTerm);

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .object(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
                .build();

    }

    // Not : getAll() ***************************
    public List<LessonProgramResponse> getAllLessonProgramByList() {

        return lessonProgramRepository
                .findAll()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    // Not : getById() **************************
    public LessonProgramResponse getLessonProgramById(Long id) {

        return lessonProgramMapper.mapLessonProgramToLessonProgramResponse(isLessonProgramExistById(id));

    }

    private LessonProgram isLessonProgramExistById(Long id){
        return lessonProgramRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE,id)));
    }

    // Not : getAllLessonProgramUnassigned() ****
    public List<LessonProgramResponse> getAllLessonProgramUnassigned() {
        // TODO : Student ekleme yapilmis mi kontrolu
        return lessonProgramRepository.findByUsers_IdNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    // Not : getAllLessonProgramAssigned() ******
    public List<LessonProgramResponse> getAllAssigned() {
        // TODO : Student eklenmis olabilir, kontrol edilmeli
        return lessonProgramRepository.findByUsers_IdNotNull()
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toList());
    }

    // Not : delete() ***************************
    public ResponseMessage deleteLessonProgramById(Long id) {
        isLessonProgramExistById(id);
        lessonProgramRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_PROGRAM_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not : getAllWithPage() *******************
    public Page<LessonProgramResponse> getAllLessonProgramByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return lessonProgramRepository.findAll(pageable)
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse);

    }

    // Not : getLessonProgramByTacherOrStudent() ********
    public Set<LessonProgramResponse> getAllLessonProgramByUser(HttpServletRequest httpServletRequest) {


        String userName = (String) httpServletRequest.getAttribute("username");

        return lessonProgramRepository.getLessonProgramByUsersUsername(userName)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());

    }

    // Not: (ODEV) getLessonProgramsByTeacherId() ******
    public Set<LessonProgramResponse> getByTeacherId(Long teacherId) {
        User teacher = userService.isUserExist(teacherId);
        if(!teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_TEACHER_MESSAGE,teacherId));
        }

        return lessonProgramRepository.findByUsers_IdEquals(teacherId)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());
    }

    // Not : ( ODEV ) getLessonProgramsByStudentId() ********
    public Set<LessonProgramResponse> getByStudentId(Long studentId) {
        User student = userService.isUserExist(studentId);
        if(!student.getUserRole().getRoleType().equals(RoleType.STUDENT)){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_STUDENT_MESSAGE,studentId));
        }

        return lessonProgramRepository.findByUsers_IdEquals(studentId)
                .stream()
                .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
                .collect(Collectors.toSet());
    }
}
