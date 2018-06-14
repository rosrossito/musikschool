package com.epam.charity.controller;

import com.epam.charity.auth.handlers.JwtAuthenticationEntryPoint;
import com.epam.charity.config.log4j.Log4j;
import com.epam.charity.dto.entity.DataWrapperDTO;
import com.epam.charity.dto.entity.SimpleMessageDTO;
import com.epam.charity.dto.entity.UserDTO;
import com.epam.charity.service.AdminService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.charity.controller.Mappings.*;

@RestController
@CrossOrigin(exposedHeaders = AUTHENTICATION_HEADER + ", " + ROLE_HEADER)
public class AdminController {
  private static @Log4j Logger logger;
  @Autowired
  private AdminService adminService;

  @PostMapping(API + ADMIN + REGISTER)
  public SimpleMessageDTO registerUser(@RequestBody UserDTO userDTO) {

    logger.warn("Registration attempt");

    adminService.registerUser(userDTO);

    return new SimpleMessageDTO("User was successfully registered");
  }

  @PutMapping(API + ADMIN + UPDATE + VAR_LOGIN)
  public SimpleMessageDTO updateUser(@PathVariable("login") String login,
      @RequestBody UserDTO userDto) {
    userDto.setLogin(login);
    adminService.updateUser(userDto);

    return new SimpleMessageDTO("User was successfully updated");
  }

  @DeleteMapping(API + ADMIN + REMOVE + VAR_LOGIN)
  public SimpleMessageDTO removeUser(@PathVariable("login") String login) {

    logger.warn("Deleting user attempt");

    adminService.removeUserByLogin(login);

    return new SimpleMessageDTO("User was successfully removed");
  }

  @PostMapping(ADMIN + LOGIN)
  public SimpleMessageDTO login(
      @RequestBody JwtAuthenticationEntryPoint.AccountCredentials credentials) {

    return new SimpleMessageDTO("Attempted to login");
  }

  @GetMapping(API + ADMIN + PREVIEW)
  public DataWrapperDTO<List<UserDTO>> getUsersPreview(
          @RequestParam(name = "_limit", required = false, defaultValue = "8") Integer limit,   // TODO: delete default value or discuss with frontend
          @RequestParam(name = "_start", required = false, defaultValue = "0") Integer offset) {

    return new DataWrapperDTO<List<UserDTO>>(adminService.getUsersPreview(limit, offset));
  }

  @RequestMapping(value = API + ADMIN + UPDATE + Mappings.VAR_ID, method = RequestMethod.GET)
  public DataWrapperDTO<UserDTO> getUser(@PathVariable("id") String id) {
    logger.warn("User requested with id " + id);
    return new DataWrapperDTO<>(adminService.getUserById(id).orElse(null));
  }
}
