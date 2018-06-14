package com.epam.charity.service;

import com.epam.charity.repository.UserRepository;
import com.epam.charity.service.AdminService;
import com.epam.charity.service.AdminServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import com.epam.charity.exceptions.ObjectNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.verify;


public class AdminServiceImplTest  {

    private static final String ID = "1";

    private AdminService adminService;
    private UserRepository mockUserRepository;

    @Before
    public void initAdminService(){
        mockUserRepository = Mockito.mock(UserRepository.class);
        adminService = new AdminServiceImpl(mockUserRepository);
    }

    @Test(expected =  ObjectNotFoundException.class)
    public void getUserByIdThrowObjectNotFoundException(){
        adminService.getUserById(ID);
    }

    @Test
    public void positiveGetUserByIdScenario(){
        mockUserRepository.getUserById(Long.parseLong(ID));
        verify(mockUserRepository).getUserById(Long.parseLong(ID));
    }
}
