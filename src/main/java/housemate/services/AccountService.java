package housemate.services;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<List<UserAccount>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    public ResponseEntity<UserAccount> getInfo(int userId) {
        UserAccount account = userRepository.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    public ResponseEntity<String> updateInfo(UserAccount account) {
        userRepository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully!");
    }

    public ResponseEntity<String> delete(int userId) {
        // Find account in database
        UserAccount account = userRepository.findByUserId(userId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot find this account!");
        }

        // Delete account
        userRepository.deleteById(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
    }
    public ResponseEntity<String> changeRole(int userId, Role role) {
        userRepository.updateRole(userId, role);
        return ResponseEntity.status(HttpStatus.OK).body("Changed role successfully!");
    }

}
