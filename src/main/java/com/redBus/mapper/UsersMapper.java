package com.redBus.mapper;

import com.redBus.model.Users;
import com.redBus.payload.UsersDto;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {
    public Users toEntity(UsersDto usersDto) {
        Users user = new Users();
        user.setName(usersDto.getName());
        user.setMobile(usersDto.getMobile());
        user.setGender(usersDto.getGender());
        return user;
    }

    public UsersDto toDto(Users user) {
        UsersDto userDto = new UsersDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setGender(user.getGender());
        userDto.setMobile(user.getMobile());
        return userDto;
    }
}
