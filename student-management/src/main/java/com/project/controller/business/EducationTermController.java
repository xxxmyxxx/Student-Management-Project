package com.project.controller.business;

import com.project.entity.business.EducationTerm;
import com.project.payload.request.business.EducationTermRequest;
import com.project.payload.response.business.EducationTermResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/educationTerms")
@RequiredArgsConstructor
public class EducationTermController {

    private final EducationTermService educationTermService;

    // Not: save() ****************************************
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> saveEducationTerm(@RequestBody @Valid EducationTermRequest educationTermRequest){

        return educationTermService.saveEducationTerm(educationTermRequest);
    }

    // Not: getById() **************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    @GetMapping("/{id}")
    public EducationTermResponse getEducationTermById(@PathVariable Long id){
        return educationTermService.getEducationTermResponseById(id);
    }

    // Not: getAll() **************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    @GetMapping("/getAll")
    public List<EducationTermResponse> getAllEducationTerms(){
        return educationTermService.getAllEducationTerms();
    }

    // Not: getAllWithPage() ******************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    @GetMapping("/getAllEducationTermsByPage")
    public Page<EducationTermResponse>  getAllEducationTermsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return educationTermService.getAllEducationTermsByPage(page,size,sort,type);
    }

    // Not: deleteById() **********************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseMessage<?> deleteEducationTermById(@PathVariable Long id){
        return educationTermService.deleteEducationTermById(id);
    }

    // Not: updateById() **********************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/update/{id}")
    public ResponseMessage<EducationTermResponse> updateEducationTerm(@PathVariable Long id,
                                                                      @RequestBody @Valid EducationTermRequest educationTermRequest){
        return educationTermService.updateEducationTerm(id, educationTermRequest);
    }


}