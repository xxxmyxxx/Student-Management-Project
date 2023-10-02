package com.project.service.business;

import com.project.entity.business.Lesson;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.LessonMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.LessonRequest;
import com.project.payload.response.business.LessonResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.LessonRepository;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final PageableHelper pageableHelper;

    // Not: save() ******************************
    public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {

        isLessonExistByLessonName(lessonRequest.getLessonName());
        // !!! DTO --> POJO
        Lesson savedLesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));

        return ResponseMessage.<LessonResponse>builder()
                .object(lessonMapper.mapLessonToLessonResponse(savedLesson))
                .message(SuccessMessages.LESSON_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private boolean isLessonExistByLessonName(String lessonName){
        boolean lessonExist =  lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName);
        if(lessonExist){
            throw new ConflictException(ErrorMessages.LESSON_ALREADY_EXIST);
        } else {
            return false;
        }
    }

    // Not: deleteById() ********************************
    public ResponseMessage deleteLessonById(Long id) {

        lessonRepository.delete(isLessonExistById(id));

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public Lesson isLessonExistById(Long id){
        return lessonRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id)));
    }

    // Not: getByName() *********************************
    public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {
        if(lessonRepository.getLessonByLessonName(lessonName).isPresent()){
            return ResponseMessage.<LessonResponse>builder()
                    .message(SuccessMessages.LESSON_FOUND)
                    .object(lessonMapper.mapLessonToLessonResponse(lessonRepository.getLessonByLessonName(lessonName).get()))
                    .build();
        } else {
            return ResponseMessage.<LessonResponse>builder()
                    .message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE_WITH_LESSON_NAME, lessonName))
                    .build();
        }
    }


    // Not: getAllWithPage() ****************************
    public Page<LessonResponse> findLessonByPage(int page, int size, String sort, String type) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return
                lessonRepository.findAll(pageable).map(lessonMapper::mapLessonToLessonResponse);
    }

    // Not: getLessonsByIdList() ************************
    public Set<Lesson> getLessonByLessonIdSet(Set<Long> idSet) {

        return idSet.stream()
                .map(this::isLessonExistById)
                .collect(Collectors.toSet());
    }


}