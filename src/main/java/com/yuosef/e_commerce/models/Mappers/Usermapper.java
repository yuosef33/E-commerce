package com.yuosef.e_commerce.models.Mappers;

import com.yuosef.e_commerce.models.Dtos.UserDto;
import com.yuosef.e_commerce.models.User;

public class Usermapper {

   public static UserDto toDto(User user){
    return new  UserDto(user.getId(),
            user.getName(),
            user.getEmail(),
            user.getMobileNumber(),
            user.getPwd(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getRoles(),
            user.getAuthProvider());
   };
    public static User toEntity(UserDto userDto){
       return new User(userDto.getId(),
               userDto.getName(),
               userDto.getEmail(),
               userDto.getMobileNumber(),
               userDto.getPwd(),
               userDto.getAuthorities(),
               userDto.getAuthProvider());
   };

}
