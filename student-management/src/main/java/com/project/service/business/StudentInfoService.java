package com.project.service.business;

import com.project.entity.business.EducationTerm;
import com.project.entity.business.Lesson;
import com.project.entity.business.StudentInfo;
import com.project.entity.enums.Note;
import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.StudentInfoMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.repository.business.StudentInfoRepository;
import com.project.service.user.TeacherService;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    //private final UserService userService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final StudentInfoMapper studentInfoMapper;

    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;
    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;

    // Not :  Save() ***********************************
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                StudentInfoRequest studentInfoRequest) {
        String teacherUsername = (String) httpServletRequest.getAttribute("username");
        User student =  teacherService.isUserExist(studentInfoRequest.getStudentId());
        if(!student.getUserRole().getRoleType().equals(RoleType.STUDENT)){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_STUDENT_MESSAGE,
                    studentInfoRequest.getStudentId()));
        }
        User teacher = teacherService.getTeacherByUsername(teacherUsername);
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());

        // !!! ayni ders icin duplicate kontrolu
        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());

        // !!! harf notu
        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(),
                studentInfoRequest.getFinalExam()));

        // !!! DTO --> POJO
        StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(studentInfoRequest,
                note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(),
                        studentInfoRequest.getFinalExam()));
        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_SAVE)
                .object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    private void checkSameLesson(Long studentId, String lessonName){
        boolean isLessonDuplicationExist = studentInfoRepository.getAllByStudentId_Id(studentId)
                .stream()
                .anyMatch(e->e.getLesson().getLessonName().equalsIgnoreCase(lessonName));
        if(isLessonDuplicationExist){
            throw  new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE, lessonName));
        }
    }

    private Double calculateExamAverage(Double midtermExam, Double finalExam){
        return ((midtermExam*midtermExamPercentage) + (finalExam*finalExamPercentage));
    }

    private Note checkLetterGrade(Double average){
        if(average<50.0){
            return Note.FF;
        } else if (average<60) {
            return Note.DD;
        } else if (average<65) {
            return Note.CC;
        } else if (average<70) {
            return Note.CB;
        } else if (average<75) {
            return Note.BB;
        } else if (average<80) {
            return Note.BA;
        } else {
            return Note.AA;
        }
    }

    // Not : Delete() **********************************
    public ResponseMessage deleteStudentInfo(Long studentInfoId) {
        StudentInfo studentInfo = isStudentInfoExistById(studentInfoId);

        studentInfoRepository.deleteById(studentInfoId);

        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public StudentInfo isStudentInfoExistById(Long id) {
        boolean isExist = studentInfoRepository.existsById(id);
        if(!isExist){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND,id));
        } else {
            return studentInfoRepository.findById(id).get();
        }
    }
}