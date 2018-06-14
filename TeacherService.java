package com.epam.charity.service;

import com.epam.charity.dto.entity.TeacherDTO;
import com.epam.charity.dto.entity.TeacherOutDTO;

import java.util.List;
import java.util.Optional;

public interface TeacherService {
  Optional<TeacherOutDTO> getTeacherById(String id);

  List<TeacherOutDTO> getTeachersPreview(Integer limit, Integer offset, String language);

  TeacherDTO updateTeacher(TeacherDTO teacherDTO);

  TeacherDTO saveTeacher(TeacherDTO teacherDTO, String language);

  TeacherOutDTO deleteTeacherById(String id);

}
