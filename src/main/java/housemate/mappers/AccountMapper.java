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
 *
 * @author Admin
 */

@Component
public class AccountMapper {

	 public UserAccount mapToEntity(RegisterAccountDTO registerAccountDTO) {
	        UserAccount userAccount = new UserAccount();

	        userAccount.setEmailAddress(registerAccountDTO.getEmail());
	        userAccount.setFullName(registerAccountDTO.getFullName());
	        userAccount.setPhoneNumber(registerAccountDTO.getPhoneNumber());
	        userAccount.setRole(Role.CUSTOMER);
	        userAccount.setEmailValidationStatus(false);

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

