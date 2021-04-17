package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Login;
import com.sweetpeatime.sweetpeatime.repositories.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/login")
public class LoginController {

    @Autowired
    LoginRepository loginRepository;

    @PostMapping("")
    public Login login(@RequestBody Login user) {
        Login loginUser = this.loginRepository.findLoginByUsernameAndPassword(user.getUsername(), user.getPassword());

        if (loginUser == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "failed");
        }

        return loginUser;
    }
}
