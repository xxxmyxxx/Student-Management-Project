package com.project.repository.business;

import com.project.entity.business.Meet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MeetRepository extends JpaRepository<Meet, Long> {

    List<Meet> getByAdvisoryTeacher_IdEquals(Long advisorTeacherId);

    List<Meet> getByStudentList_IdEquals(Long studentId);

    Page<Meet> findByAdvisoryTeacher_IdEquals(Long id, Pageable pageable);

}
