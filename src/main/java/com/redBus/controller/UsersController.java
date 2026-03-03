package com.redBus.controller;

import com.redBus.payload.UsersDto;
import com.redBus.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsersDto> registerUser(
            @Valid @RequestBody UsersDto usersDto) {

        return ResponseEntity.ok(usersService.registerUser(usersDto));
    }
}