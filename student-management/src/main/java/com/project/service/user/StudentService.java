package com.project.service.user;

import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.StudentRequest;
import com.project.payload.request.user.StudentRequestWithoutPassword;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.repository.user.UserRepository;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;

    // Not : saveStudent() *****************************
    public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {
        // !!! id kontrol
        User advisorTeacher = userRepository.findById(studentRequest.getAdvisorTeacherId()).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,studentRequest.getAdvisorTeacherId())));
        // !!! acaba Advisor mi ??
        if(advisorTeacher.getIsAdvisor()!=Boolean.TRUE){
            throw  new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE,advisorTeacher.getId()));
        }

        // !!! unique kontrol
        uniquePropertyValidator.checkDuplicate(studentRequest.getUsername(),
                studentRequest.getSsn(),
                studentRequest.getPhoneNumber(),
                studentRequest.getEmail());

        // !!! DTO --> POJO
        User student = userMapper.mapStudentRequestToUser(studentRequest);
        student.setAdvisorTeacherId(advisorTeacher.getId());
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);
        student.setIsAdvisor(Boolean.FALSE);
        // !!! Ogrenci numarasi setleniyor
        student.setStudentNumber(getLastNumber());

        return ResponseMessage.<StudentResponse>builder()
                .object(userMapper.mapUserToStudentResponse(userRepository.save(student)))
                .message(SuccessMessages.STUDENT_SAVE)
                .build();
    }

    private int getLastNumber(){

        if(!userRepository.findStudent(RoleType.STUDENT)) {
            return 1000;
        }

        return userRepository.getMaxStudentNumber() + 1 ;

    }

    // Not: updateStudentForStudents() ********************
    public ResponseEntity<String> updateStudent(StudentRequestWithoutPassword studentRequest, HttpServletRequest request) {

        String userName = (String) request.getAttribute("username");
        User student = userRepository.findByUsernameEquals(userName);

        uniquePropertyValidator.checkUniqueProperties(student, studentRequest );

        student.setMotherName(studentRequest.getMotherName());
        student.setFatherName(studentRequest.getFatherName());
        student.setBirthDay(studentRequest.getBirthDay());
        student.setEmail(studentRequest.getEmail());
        student.setPhoneNumber(studentRequest.getPhoneNumber());
        student.setBirthPlace(studentRequest.getBirthPlace());
        student.setGender(studentRequest.getGender());
        student.setName(studentRequest.getName());
        student.setSurname(studentRequest.getSurname());
        student.setSsn(studentRequest.getSsn());

        userRepository.save(student);
        String message = SuccessMessages.USER_UPDATE_MESSAGE;
        return ResponseEntity.ok(message);
    }

    // Not: updateStudent() ******************************
    public ResponseMessage<StudentResponse> updateStudentForManagers(Long userId, StudentRequest studentRequest) {
        User user = isUserExist(userId);
        if(!user.getUserRole().getRoleType().equals(RoleType.STUDENT)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_STUDENT_MESSAGE, userId));
        }

        uniquePropertyValidator.checkUniqueProperties(user, studentRequest);

        User updatedStudent = userMapper.mapStudentRequestToUpdatedUser(studentRequest, userId);
        updatedStudent.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        updatedStudent.setAdvisorTeacherId(studentRequest.getAdvisorTeacherId());
        updatedStudent.setStudentNumber(user.getStudentNumber());
        updatedStudent.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        updatedStudent.setActive(true);

        return ResponseMessage.<StudentResponse> builder()
                .object(userMapper.mapUserToStudentResponse(userRepository.save(updatedStudent)))
                .message(SuccessMessages.STUDENT_UPDATE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public User isUserExist(Long userId){
        return  userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));
    }
}