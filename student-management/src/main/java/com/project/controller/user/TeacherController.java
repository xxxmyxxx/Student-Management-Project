package com.project.controller.user;

import com.project.payload.request.user.TeacherRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.StudentResponse;
import com.project.payload.response.user.TeacherResponse;
import com.project.payload.response.user.UserResponse;
import com.project.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // Not : saveTeacher() ******************************
    @PostMapping("/save") // http://localhost:8080/teacher/save  + POST  + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<TeacherResponse>> saveTeacher(@RequestBody @Valid TeacherRequest teacherRequest) {
        return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
    }

    // Not: updateTeacherById() **************************
    @PutMapping("/update/{userId}") // http://localhost:8080/teacher/update/5 + PUT + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> updateTeacherForManagers(@RequestBody @Valid TeacherRequest teacherRequest,
                                                                     @PathVariable Long userId) {
        return teacherService.updateTeacherForManagers(teacherRequest, userId);
    }

    // Not: GetAllStudentByAdvTeacherUserName() ********************
    // Bir Rehber ogretmenin kendi ogrencilerinin tamamini getiren method

    @GetMapping("/getAllStudentByAdvisorUsername")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllStudentByAdvisorUsername(HttpServletRequest request){
        String userName = request.getHeader("username");
        return teacherService.getAllStudentByAdvisorUsername(userName);
    }

    // TODO : AddLessonProgramToTeachersLessonProgram eklenecek

    // Not: SaveAdvisorTeacherByTeacherId() ****************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PatchMapping("/saveAdvisorTeacher/{teacherId}") // http://localhost:8080/teacher/saveAdvisorTeacher/1 + Patch
    public ResponseMessage<UserResponse> saveAdvisorTeacher(@PathVariable Long teacherId){
        return teacherService.saveAdvisorTeacher(teacherId);
    }
    //bu delate mappinde olabilir pach mappingede olabilir icerisin sadece 1 fieldi silinecek
    // Not : deleteAdvisorTeacherById() ********************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @DeleteMapping("/deleteAdvisorTeacherById/{id}")
    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(@PathVariable Long id){
        return teacherService.deleteAdvisorTeacherById(id);
    }

    // Not : getAllAdvisorTeacher() ************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllAdvisorTeacher")
    public List<UserResponse> getAllAdvisorTeacher() {
        return teacherService.getAllAdvisorTeacher();
    }


}