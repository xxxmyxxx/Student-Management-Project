package com.project.controller.user;

import com.project.payload.request.business.ChooseLessonProgramWithId;
import com.project.payload.request.user.StudentRequest;
import com.project.payload.request.user.StudentRequestWithoutPassword;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    // Not : saveStudent() *****************************
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<ResponseMessage<StudentResponse>> saveStudent(@RequestBody @Valid StudentRequest studentRequest){
        return ResponseEntity.ok(studentService.saveStudent(studentRequest));
    }

    // Not: updateStudentForStudents() ********************
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @PatchMapping("/update")
    public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentRequestWithoutPassword studentRequestWithoutPassword,
                                                HttpServletRequest request){
        return studentService.updateStudent(studentRequestWithoutPassword, request);
    }

    // Not: updateStudent() ******************************
    // yoneticilerin ogrenci bilgilerini guncelleme islemi
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PutMapping("/update/{userId}")
    public ResponseMessage<StudentResponse> updateStudentForManagers(@PathVariable Long userId,
                                                                     @RequestBody @Valid StudentRequest studentRequest) {
        return studentService.updateStudentForManagers(userId, studentRequest);
    }

    // Not: addLessonProgramToStudentLessonsProgram() *********
    // !!! Student kendine lessonProgram ekliyor
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @PostMapping("/addLessonProgramToStudent")
    public ResponseMessage<StudentResponse> addLessonProgram(HttpServletRequest request,
                                                             @RequestBody @Valid ChooseLessonProgramWithId chooseLessonProgramWithId){
        String userName = (String) request.getAttribute("username");
        return studentService.addLessonProgramToStudent(userName, chooseLessonProgramWithId);
    }

    // Not: ChangeActıveStatusOfStudent() *********************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PatchMapping("/changeStatus")
    public ResponseMessage changeStatusOfStudent(@RequestParam Long id, @RequestParam boolean status){
        return studentService.changeStatusOfStudent(id,status);
    }





}
