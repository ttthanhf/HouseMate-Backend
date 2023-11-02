/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.UserAccount;
import housemate.models.UserDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class UserMapper {

    public UserDTO mapToDto(UserAccount userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setFullName(userEntity.getFullName());
        userDTO.setAvatar(userEntity.getAvatar());
        return userDTO;
    }
}
