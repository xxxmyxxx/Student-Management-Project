package com.project.repository.business;

import com.project.entity.business.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentInfoRepository extends JpaRepository<StudentInfo,Long> {
    List<StudentInfo> getAllByStudentId_Id(Long studentId);
}
