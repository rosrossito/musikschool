package com.epam.charity.controller;

import com.epam.charity.dto.entity.DataWrapperDTO;
import com.epam.charity.dto.entity.TeacherDTO;
import com.epam.charity.dto.entity.TeacherOutDTO;
import com.epam.charity.service.TeacherService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Logger
@RestController
@RequestMapping(Mappings.TEACHERS)
public class TeacherRestController {
  private TeacherService teacherService;

  @Autowired
  public TeacherRestController(TeacherService teacherService) {
    this.teacherService = teacherService;
  }

  @RequestMapping(value = Mappings.VAR_ID, method = RequestMethod.GET)
  public DataWrapperDTO<TeacherOutDTO> getTeacherById(@PathVariable String id) {
    return new DataWrapperDTO<>(teacherService.getTeacherById(id).orElse(null));
  }

  @RequestMapping(method = RequestMethod.GET)
  public DataWrapperDTO<List<TeacherOutDTO>> getTeachersPreview(
          @RequestParam(name = "_limit", required = false) Integer limit,
          @RequestParam(name = "_start", required = false, defaultValue = "0") Integer offset,
          @RequestParam(name = "_lang", required = false, defaultValue = "uk") String language) {
    return new DataWrapperDTO<>(teacherService.getTeachersPreview(limit, offset, language));
  }

  @PutMapping(value = Mappings.VAR_ID)
  public TeacherDTO updateTeacher(@PathVariable String id,
                                  @RequestBody TeacherDTO teacherDTO) {
    teacherDTO.setId(id);
    return teacherService.updateTeacher(teacherDTO);
  }

  @RequestMapping(method = RequestMethod.POST)
  public TeacherDTO saveTeacher(@RequestBody TeacherDTO teacherDTO,
                                @RequestParam(name = "_lang", required = false, defaultValue = "uk") String language) {
    return teacherService.saveTeacher(teacherDTO, language);
  }

  @DeleteMapping(value = Mappings.VAR_ID)
  public DataWrapperDTO<TeacherOutDTO> deleteTeacher(@PathVariable String id) {
    return new DataWrapperDTO<>(teacherService.deleteTeacherById(id));
  }

}
