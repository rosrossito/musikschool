package com.epam.charity.service;


import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.*;
import com.epam.charity.exceptions.InvalidParamException;
import com.epam.charity.exceptions.ObjectNotFoundException;
import com.epam.charity.repository.TeacherDepartmentRepository;
import com.epam.charity.repository.TeacherRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class TeacherServiceImpl implements TeacherService {
  private static @Log4j Logger logger;

  private TeacherRepository repository;
  private TeacherDepartmentRepository teacherDepartmentRepository;
  private ImageService imageService;


  @Autowired
  public TeacherServiceImpl(TeacherRepository repository, ImageService imageService,
                            TeacherDepartmentRepository teacherDepartmentRepository) {
    this.repository = repository;
    this.imageService = imageService;
    this.teacherDepartmentRepository = teacherDepartmentRepository;
  }

  @Override
  public Optional<TeacherOutDTO> getTeacherById(String id) {

    Optional<TeacherOutDTO> teacher = repository.getTeacherById(Long.parseLong(id));
    if (teacher.isPresent()) {
      insertImgTitleAndLink(teacher);
      return teacher;
    } else {
      throw ObjectNotFoundException.builder()
              .title(format("Teacher with id=%s not found", id)).detail("Teacher not found")
              .pointer("").build();
    }
  }

  @Override
  public List<TeacherOutDTO> getTeachersPreview(Integer limit, Integer offset, String language) {
    List<TeacherOutDTO> teachersPreview = repository.getTeachersPreview(limit, offset, language);
    teachersPreview.forEach(this::insertImgTitleAndLink);
    return teachersPreview;
  }

  @Override
  @Transactional
  public TeacherDTO updateTeacher(TeacherDTO teacherDTO) {
    Optional<TeacherOutDTO> teacherNeededToUpdate = getTeacherById(teacherDTO.getId());

    if (teacherNeededToUpdate.isPresent()) {

      teacherDepartmentRepository.updateTeacherDepartment(teacherDTO);
      TeacherDTO teacher = repository.updateTeacher(teacherDTO);
      insertImgTitleAndLink(teacher);
      return teacher;
    } else{
      throw ObjectNotFoundException.builder()
              .detail(format("Teacher with id=%s not found", teacherDTO.getId())).title("Teacher not found")
              .pointer("").build();
    }
  }

  @Override
  public TeacherDTO saveTeacher(TeacherDTO teacherDTO, String language) {

    if(language.isEmpty()){
      logger.info("Can't create teacher: no language selected!");
      throw InvalidParamException.builder().detail("No language selected!").build();
    }

    teacherDTO = repository.saveTeacher(teacherDTO, language);

    if (!teacherDTO.getDepartmentDTOSet().isEmpty()){
      teacherDepartmentRepository.insertTeacherDepartmentRecord(teacherDTO);
    }

    insertImgTitleAndLink(teacherDTO);
    return teacherDTO;
  }

  @Override
  public TeacherOutDTO deleteTeacherById(String id) {
    Optional<TeacherOutDTO> teachersNeededToDelete = getTeacherById(id);
    if (teachersNeededToDelete.isPresent()) {

      if (!teachersNeededToDelete.get().getDepartmentDTOSet().isEmpty()){
        teacherDepartmentRepository.deleteAllDepartmentsOfTeacher(id);
      }
      return repository.deleteTeacherById(Long.parseLong(id));
    }
    throw ObjectNotFoundException.builder().detail(format("Teacher with id=%s not found", id))
            .title("Teacher not found").pointer("").build();
  }

  private void insertImgTitleAndLink(Optional <TeacherOutDTO> teacherDTO) {
    ImageDTO filledImageDTO = imageService.getById(teacherDTO.get().getImg().getId());
    teacherDTO.get().getImg().setTitle(filledImageDTO.getTitle());
    teacherDTO.get().getImg().setLink(filledImageDTO.getLink());
  }

  private void insertImgTitleAndLink(TeacherOutDTO teacherDTO) {
    ImageDTO filledImageDTO = imageService.getById(teacherDTO.getImg().getId());
    teacherDTO.getImg().setTitle(filledImageDTO.getTitle());
    teacherDTO.getImg().setLink(filledImageDTO.getLink());
  }

  private void insertImgTitleAndLink(TeacherDTO teacherDTO) {
    ImageDTO filledImageDTO = imageService.getById(teacherDTO.getImg().getId());
    teacherDTO.getImg().setTitle(filledImageDTO.getTitle());
    teacherDTO.getImg().setLink(filledImageDTO.getLink());
  }

  private void insertImgTitleAndLink(DepartmentDTO department) {
    ImageDTO filledImageDTO = imageService.getById(department.getImg().getId());
    department.getImg().setLink(filledImageDTO.getLink());
    department.getImg().setTitle(filledImageDTO.getTitle());
  }
}