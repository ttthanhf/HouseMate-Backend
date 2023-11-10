/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.models.CreateAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.models.UpdateAccountDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Admin
 */

@Component
public class AccountMapper {

    final String DEFAULT_AVATAR = "https://scontent.fsgn2-9.fna.fbcdn.net/v/t1.15752-9/384469032_6609223889131065_8293022876449520388_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=ae9488&_nc_ohc=gjDXwSBmi3YAX-hNO9i&_nc_ht=scontent.fsgn2-9.fna&oh=03_AdTYPieo_8M2sWscLr-rykTpN-IAaBS8JRWarwlkJQpKdA&oe=6540E586";
    final int MAX_PROFICIENCY_SCORE = 100;
    final int MAX_AVERAGE_RATING = 5;

    public UserAccount mapToEntity(RegisterAccountDTO registerAccountDTO) {
        UserAccount userAccount = new UserAccount();

        userAccount.setEmailAddress(registerAccountDTO.getEmail());
        userAccount.setFullName(registerAccountDTO.getFullName());
        userAccount.setPhoneNumber(registerAccountDTO.getPhoneNumber());
        userAccount.setRole(Role.CUSTOMER);
        userAccount.setEmailValidationStatus(false);
        userAccount.setPasswordHash(registerAccountDTO.getPassword());
        userAccount.setAvatar(DEFAULT_AVATAR);
        userAccount.setCreatedAt(LocalDateTime.now());

	    return userAccount;
	    }

    public UserAccount mapToEntity(CreateAccountDTO createAccountDTO) {
        UserAccount userAccount = new UserAccount();

        userAccount.setFullName(createAccountDTO.getFullName());
        userAccount.setDateOfBirth(createAccountDTO.getDateOfBirth());
        userAccount.setSex(createAccountDTO.getSex());
        userAccount.setPhoneNumber(createAccountDTO.getPhone());
        userAccount.setIdentityCard(createAccountDTO.getIdentityCard());
        userAccount.setEmailAddress(createAccountDTO.getEmail());
        userAccount.setAddress(createAccountDTO.getAddress());
        userAccount.setRole(Role.STAFF);
        userAccount.setProficiencyScore(MAX_PROFICIENCY_SCORE);
        userAccount.setAvgRating(MAX_AVERAGE_RATING);

        userAccount.setEmailValidationStatus(true);

        return userAccount;
    }

	    public UserAccount updateAccount(UserAccount currentAccount, UpdateAccountDTO updatedAccount) {
	        currentAccount.setFullName(updatedAccount.getFullName());
	        currentAccount.setPhoneNumber(updatedAccount.getPhoneNumber());
	        currentAccount.setEmailAddress(updatedAccount.getEmailAddress());
	        currentAccount.setEmailAddress(updatedAccount.getEmailAddress());

	        return currentAccount;
	    }
}

