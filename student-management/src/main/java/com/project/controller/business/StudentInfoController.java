package com.project.controller.business;

import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;


    // Not :  Save() ***********************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(@RequestBody @Valid StudentInfoRequest studentInfoRequest,
                                                                HttpServletRequest httpServletRequest){
        return studentInfoService.saveStudentInfo(httpServletRequest,studentInfoRequest);
    }

    // Not : Delete() **********************************
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @DeleteMapping("/delete/{studentInfoId}")
    public ResponseMessage delete(@PathVariable Long studentInfoId){
        return studentInfoService.deleteStudentInfo(studentInfoId);
    }
}