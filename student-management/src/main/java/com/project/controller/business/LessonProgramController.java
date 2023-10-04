package com.project.controller.business;

import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    // Not :  Save() ****************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save + POST + JSON
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid LessonProgramRequest lessonProgramRequest){
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }

    // Not : getAll() ***************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    @GetMapping("/getAll")
    public List<LessonProgramResponse> getAllLessonProgramByList(){
        return lessonProgramService.getAllLessonProgramByList();
    }

    // Not : getById() **************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getById/{id}")
    public  LessonProgramResponse getLessonProgramById(@PathVariable Long id){
        return lessonProgramService.getLessonProgramById(id);
    }

    // Not : getAllLessonProgramUnassigned() ****
    //Teacher atamasi yapilmamis butun dersprogramlari getirecegiz
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    @GetMapping("/getAllUnassigned")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllLessonProgramUnassigned();
    }

    // Not : getAllLessonProgramAssigned() ******
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    @GetMapping("/getAllAssigned")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllAssigned();
    }

    // Not : delete() ***************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage deleteLessonProgramById(@PathVariable Long id){
        return lessonProgramService.deleteLessonProgramById(id);
    }

    // Not : getAllWithPage() *******************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    @GetMapping("/getAllLessonProgramByPage")
    public Page<LessonProgramResponse> getAllLessonProgramByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return  lessonProgramService.getAllLessonProgramByPage(page,size,sort,type) ;
    }


    // Not : getLessonProgramByTeacher() ********
    // bir Ogretmen kendine ait lessonProgramlari getiriyor
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllLessonProgramByTeacher")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacherUsername(HttpServletRequest httpServletRequest) {
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    // Not : getLessonProgramByStudent() ********
    // bir Ogrenci kendine ait lessonProgramlari getiriyor
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllLessonProgramByStudent")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest httpServletRequest) {
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    // Not: (ODEV) getLessonProgramsByTeacherId() ******
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllLessonProgramByTeacherId/{teacherId}")
    public Set<LessonProgramResponse> getByTeacherId(@PathVariable Long teacherId){
        return lessonProgramService.getByTeacherId(teacherId);
    }

    // Not : ( ODEV ) getLessonProgramsByStudentId() ********
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllLessonProgramByStudentId/{studentId}")
    public Set<LessonProgramResponse> getByStudentId(@PathVariable Long studentId){
        return lessonProgramService.getByStudentId(studentId);
    }

    // Not: (Odev) Update() **********************************

}
