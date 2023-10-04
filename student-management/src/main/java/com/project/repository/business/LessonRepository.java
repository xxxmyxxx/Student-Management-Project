package com.project.repository.business;

import com.project.entity.business.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;
import java.util.Set;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsLessonByLessonNameEqualsIgnoreCase(String lessonName);

    Optional<Lesson> getLessonByLessonName(String lessonName);

    @Query(value = "SELECT l FROM Lesson l WHERE l.id IN :lessonId")
    Set<Lesson> getLessonByLessonIdList(Set<Long> lessonId);


    boolean existsByLessonName(String lessonName);

}
