package com.project.service.user;

import com.project.entity.business.LessonProgram;
import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.exception.BadRequestException;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.ChooseLessonTeacherRequest;
import com.project.payload.request.user.TeacherRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import com.project.payload.response.user.UserResponse;
import com.project.repository.user.UserRepository;
import com.project.service.business.LessonProgramService;
import com.project.service.validator.DateTimeValidator;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final LessonProgramService lessonProgramService;
    private final DateTimeValidator dateTimeValidator;

    // Not : saveTeacher() ******************************
    public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {

        Set<LessonProgram> lessonProgramSet =
                lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

        // !!! unique kontrolu
        uniquePropertyValidator.checkDuplicate(teacherRequest.getUsername(), teacherRequest.getSsn(),
                teacherRequest.getPhoneNumber(), teacherRequest.getEmail());

        // !!! DTO --> POJO
        User teacher = userMapper.mapTeacherRequestToUser(teacherRequest);
        // !!! POJO da olmasi gerekipde DTO dan gelmeyen degerleri setliyoruz
        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
        teacher.setLessonsProgramList(lessonProgramSet);
        // !!! password encode
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        // !!! isAdvisor
        if(teacherRequest.getIsAdvisorTeacher()) { // BOOLEAN.TRUE.equals(teacherRequest.getIsAdvisorTeacher())
            teacher.setIsAdvisor(Boolean.TRUE);
        } else teacher.setIsAdvisor(Boolean.FALSE);

        User savedTeacher = userRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.TEACHER_SAVE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .build();
    }

    // Not: updateTeacherById() **************************
    public ResponseMessage<TeacherResponse> updateTeacherForManagers(TeacherRequest teacherRequest, Long userId) {

        // !!! var mi yok mu kontrolu
        User user = isUserExist(userId);
        // !!! parametrede gelen id, bir teacher a ait mi kontrolu ?
        if(!user.getUserRole().getRoleType().equals(RoleType.TEACHER)){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_TEACHER_MESSAGE,userId));
        }
        Set<LessonProgram> lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

        // !!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, teacherRequest);
        // !!! DTO --> POJO
        User updatedTeacher = userMapper.mapTeacherRequestToUpdatedUser(teacherRequest,userId);
        // !!! Password encode
        updatedTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
        updatedTeacher.setLessonsProgramList(lessonPrograms);
        updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));

        User savedTeacher = userRepository.save(updatedTeacher);

        return ResponseMessage.<TeacherResponse>builder()
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .message(SuccessMessages.TEACHER_UPDATE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public  User isUserExist(Long userId){
        return  userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));
    }

    // Not: GetAllStudentByAdvTeacherUserName() ********************
    public List<StudentResponse> getAllStudentByAdvisorUsername(String userName) {

        User teacher = getTeacherByUsername(userName);
        // verilen username in sahibi olan user Advisor mi ??
        if(Boolean.FALSE.equals(teacher.getIsAdvisor())){
            throw  new BadRequestException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, userName));
        }

        return userRepository.findByAdvisorTeacherId(teacher.getId())
                .stream()
                .map(userMapper::mapUserToStudentResponse)
                .collect(Collectors.toList());

    }

    public User getTeacherByUsername(String teacherUsername){
        return userRepository.findByUsernameEquals(teacherUsername);
    }

    // Not: SaveAdvisorTeacherByTeacherId() ****************************
    public ResponseMessage<UserResponse> saveAdvisorTeacher(Long teacherId) {
        // !!! id kontrol
        User teacher = isUserExist(teacherId);
        // !!! id ile gelen user , teacher mi ??
        if(!(teacher.getUserRole().getRoleType().equals(RoleType.TEACHER))){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE, teacherId));
        }
        // !!! zaten advisor ise
        if(Boolean.TRUE.equals(teacher.getIsAdvisor())) {
            throw  new ConflictException(String.format(ErrorMessages.ALREADY_EXIST_ADVISOR_MESSAGE, teacherId));
        }

        teacher.setIsAdvisor(Boolean.TRUE);
        userRepository.save(teacher);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_SAVE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    // Not : deleteAdvisorTeacherById() ********************************
    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(Long teacherId) {
        User teacher = isUserExist(teacherId);
        // !!! id ile gelen user , teacher mi ??
        if(!(teacher.getUserRole().getRoleType().equals(RoleType.TEACHER))){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE, teacherId));
        }
        if(Boolean.FALSE.equals(teacher.getIsAdvisor())){
            throw  new ConflictException(String.format(ErrorMessages.NOT_EXIST_ADVISOR_MESSAGE, teacherId));
        }

        teacher.setIsAdvisor(Boolean.FALSE);
        userRepository.save(teacher);

        // !!! silinen adv.Teacher in studentlari varsa bu ilsikiyi koparmamiz gerekiyor
        List<User> allStudents = userRepository.findByAdvisorTeacherId(teacherId);
        if(!allStudents.isEmpty()){
            allStudents.forEach(students->students.setAdvisorTeacherId(null));
        }

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .httpStatus(HttpStatus.OK)
                .build();

    }

    // Not : getAllAdvisorTeacher() ************************************
    public List<UserResponse> getAllAdvisorTeacher() {

        return userRepository.findAllByAdvisor(Boolean.TRUE)
                .stream()
                .map(userMapper::mapUserToUserResponse)
                .collect(Collectors.toList());
    }

    // Not : AddLessonProgramToTeachersLessonProgram **************
    public ResponseMessage<TeacherResponse> addLessonProgram(ChooseLessonTeacherRequest chooseLessonTeacherRequest) {
        User teacher = isUserExist(chooseLessonTeacherRequest.getTeacherId());
        if(!teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_TEACHER_MESSAGE,
                    chooseLessonTeacherRequest.getTeacherId()));
        }
        // !!! requestten gelen LessonProgramlari getiriyoruz
        Set<LessonProgram> lessonPrograms =lessonProgramService.getLessonProgramById(
                chooseLessonTeacherRequest.getLessonProgramId());
        // !!! mevcut  LessonProgramlari getiriyoruz
        Set<LessonProgram> teachersLessonProgram =  teacher.getLessonsProgramList();

        // !!! LessonProgramlar icin cakisma kontrolu :
        dateTimeValidator.checkLessonPrograms(teachersLessonProgram, lessonPrograms);
        teachersLessonProgram.addAll(lessonPrograms);
        teacher.setLessonsProgramList(teachersLessonProgram);
        User updatedTeacher = userRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapUserToTeacherResponse(updatedTeacher))
                .build();
    }
}