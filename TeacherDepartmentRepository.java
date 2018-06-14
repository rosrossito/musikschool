package com.epam.charity.repository;

import com.epam.charity.dto.entity.TeacherDTO;

import java.util.Set;

public interface TeacherDepartmentRepository {

    void updateTeacherDepartment (TeacherDTO teacherDTO);
    void deleteAllDepartmentsOfTeacher(String id);
    void insertTeacherDepartmentRecord(TeacherDTO teacherDTO);
    Set<Long> departmentsToSetDepartmentId(Set<String> departments);
    void deleteTeacherDepartment (String teacherId, String departmentId);

}
