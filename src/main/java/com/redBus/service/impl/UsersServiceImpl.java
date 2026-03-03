package com.redBus.service.impl;

import com.redBus.mapper.UsersMapper;
import com.redBus.model.Users;
import com.redBus.payload.UsersDto;
import com.redBus.repository.UsersRepo;
import com.redBus.service.UsersService;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepo usersRepo;
    private final UsersMapper usersMapper;

    public UsersServiceImpl(UsersRepo usersRepo, UsersMapper usersMapper) {
        this.usersRepo = usersRepo;
        this.usersMapper = usersMapper;
    }

    @Override
    public UsersDto registerUser(UsersDto usersDto) {
        if (usersRepo.existsByMobile(usersDto.getMobile())) {
            throw new RuntimeException("Mobile number already registered");
        }

        Users user = usersMapper.toEntity(usersDto);
        Users savedUser = usersRepo.save(user);
        return usersMapper.toDto(savedUser);
    }
}
