/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.models.RegisterAccountDTO;
import housemate.models.UpdateAccountDTO;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */

@Component
public class AccountMapper {

    final String DEFAULT_AVATAR = "https://scontent.fsgn2-9.fna.fbcdn.net/v/t1.15752-9/384469032_6609223889131065_8293022876449520388_n.jpg?_nc_cat=103&ccb=1-7&_nc_sid=ae9488&_nc_ohc=gjDXwSBmi3YAX-hNO9i&_nc_ht=scontent.fsgn2-9.fna&oh=03_AdTYPieo_8M2sWscLr-rykTpN-IAaBS8JRWarwlkJQpKdA&oe=6540E586";

    public UserAccount mapToEntity(RegisterAccountDTO registerAccountDTO) {
        UserAccount userAccount = new UserAccount();

        userAccount.setEmailAddress(registerAccountDTO.getEmail());
        userAccount.setFullName(registerAccountDTO.getFullName());
        userAccount.setPhoneNumber(registerAccountDTO.getPhoneNumber());
        userAccount.setRole(Role.CUSTOMER);
        userAccount.setEmailValidationStatus(false);
        userAccount.setPasswordHash(registerAccountDTO.getPassword());
        userAccount.setAvatar(DEFAULT_AVATAR);

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

