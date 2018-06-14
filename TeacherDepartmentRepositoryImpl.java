package com.epam.charity.repository;

import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.DepartmentDTO;
import com.epam.charity.dto.entity.NewsDTO;
import com.epam.charity.dto.entity.TeacherDTO;
import com.epam.charity.jooq.dto.tables.Department;
import com.epam.charity.jooq.dto.tables.DepartmentStaff;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class TeacherDepartmentRepositoryImpl implements TeacherDepartmentRepository {

    private DSLContext dslContext;
    private static @Log4j Logger logger;

    @Autowired
    public TeacherDepartmentRepositoryImpl (DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public void updateTeacherDepartment(TeacherDTO teacherDTO) {
        deleteAllDepartmentsOfTeacher(teacherDTO.getId());
        insertTeacherDepartmentRecord(teacherDTO);
    }

    @Override
    public void deleteAllDepartmentsOfTeacher(String id) {
        this.dslContext.select(Department.DEPARTMENT.ID).from(Department.DEPARTMENT)
                .fetch().getValues(Department.DEPARTMENT.ID)
                .stream()
                .forEach(departmentId ->
                        this.dslContext
                                .deleteFrom(DepartmentStaff.DEPARTMENT_STAFF)
                                .where(DepartmentStaff.DEPARTMENT_STAFF.STAFF_ID.eq(Long.valueOf(id)))
                                .and(DepartmentStaff.DEPARTMENT_STAFF.DEPARTMENT_ID.eq(departmentId)).execute());
    }

    public void insertTeacherDepartmentRecord(TeacherDTO teacherDTO) {
        Set<String> departments = teacherDTO.getDepartmentDTOSet().stream()
                .map(DepartmentDTO::getId)
                .collect(Collectors.toSet());
        Set<Long> departmentsIds = departmentsToSetDepartmentId(departments);

        for (Long id:departmentsIds) {
            this.dslContext
                    .insertInto(DepartmentStaff.DEPARTMENT_STAFF, DepartmentStaff.DEPARTMENT_STAFF.DEPARTMENT_ID, DepartmentStaff.DEPARTMENT_STAFF.STAFF_ID)
                    .values(id, Long.valueOf(teacherDTO.getId()))
                    .execute();
        }

//        departmentsIds.stream()
//                .forEach(departmentId ->
//                this.dslContext
//                .insertInto(DepartmentStaff.DEPARTMENT_STAFF, DepartmentStaff.DEPARTMENT_STAFF.DEPARTMENT_ID,
//                        DepartmentStaff.DEPARTMENT_STAFF.STAFF_ID)
//                .values(departmentId, Long.valueOf(teacherDTO.getId())).execute());
    }

    @Override
    public Set<Long> departmentsToSetDepartmentId(Set<String> departments) {
        return departments.stream()
                .map(department -> this.dslContext
                        .select(Department.DEPARTMENT.ID)
                        .from(Department.DEPARTMENT)
                        .where(Department.DEPARTMENT.ID.eq(Long.valueOf(department)))
                        .fetchOne())
                .map(departmentRecord -> departmentRecord.get(Department.DEPARTMENT.ID))
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteTeacherDepartment(String teacherId, String departmentId) {
                        this.dslContext
                                .deleteFrom(DepartmentStaff.DEPARTMENT_STAFF)
                                .where(DepartmentStaff.DEPARTMENT_STAFF.STAFF_ID.eq(Long.valueOf(teacherId)))
                                .and(DepartmentStaff.DEPARTMENT_STAFF.DEPARTMENT_ID.eq(Long.valueOf(departmentId)))
                                .execute();
    }
}