package com.project.repository.user;

import com.project.entity.enums.RoleType;
import com.project.entity.user.User;
import com.project.payload.response.user.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameEquals(String username);

    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userRole.roleName =:roleName")
    Page<User> findByUserByRole(String roleName, Pageable pageable);

    List<User> getUserByNameContaining(String name); // derived methods , derived query

    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.userRole.roleType = ?1")
    long countAdmin(RoleType roleType);

    List<User> findByAdvisorTeacherId(Long id);
}
