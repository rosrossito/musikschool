package com.epam.charity.service;

import com.epam.charity.dto.entity.TeacherDTO;
import com.epam.charity.dto.entity.TeacherOutDTO;
import com.epam.charity.exceptions.ObjectNotFoundException;
import com.epam.charity.repository.TeacherDepartmentRepository;
import com.epam.charity.repository.TeacherRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class TeacherServiceImplTest  {

    private static final String ID = "1";
    private static final Integer limit = 3;
    private static final Integer offset = 3;
    private static final String lang = "uk";
    private TeacherService teacherService;
    private TeacherRepository mockTeacherRepository;
    private ImageService mockImageService;
    private TeacherDepartmentRepository mockTeacherDepartmentRepository;

    @Before
    public void initAdminService(){
        mockTeacherRepository = Mockito.mock(TeacherRepository.class);
        mockImageService = Mockito.mock(ImageService.class);
        mockTeacherDepartmentRepository = Mockito.mock(TeacherDepartmentRepository.class);
        teacherService = new TeacherServiceImpl(mockTeacherRepository, mockImageService, mockTeacherDepartmentRepository);
    }

    @Test(expected =  ObjectNotFoundException.class)
    public void getTeacherByIdThrowObjectNotFoundException(){
        teacherService.getTeacherById(ID);
    }

    @Test
    public void positiveGetTeacherByIdScenario(){
        TeacherOutDTO teacherOutDTO = new TeacherOutDTO();
        when(mockTeacherRepository.getTeacherById(Long.parseLong(ID))).thenReturn(ofNullable(teacherOutDTO));
    }

    @Test
    public void positiveGetTeachersPreviewScenario(){
        List<TeacherOutDTO> teachersPreview = new ArrayList<>();
        when(mockTeacherRepository.getTeachersPreview(limit, offset, lang)).thenReturn(teachersPreview);
        teacherService.getTeachersPreview(limit, offset, lang);
        verify(mockTeacherRepository).getTeachersPreview(limit, offset, lang);
    }

    @Test
    public void positiveSaveTeacherScenario(){
        TeacherDTO teacherDTO = new TeacherDTO();
        when(mockTeacherRepository.saveTeacher(teacherDTO, lang)).thenReturn(teacherDTO);
    }
}