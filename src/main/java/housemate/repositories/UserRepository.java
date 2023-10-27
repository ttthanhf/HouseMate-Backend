/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import org.springframework.data.jpa.repository.*;
<<<<<<< HEAD
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
=======
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)

/**
 *
 * @author ThanhF
 */
@Repository
public interface UserRepository extends JpaRepository<UserAccount, Integer> {

<<<<<<< HEAD
    UserAccount findByEmailAddress(String emailAddress);
=======
    // @Query("SELECT u FROM UserAccount u WHERE u.email_address = :emailAddress")
    // UserAccount findByEmailAddress(@Param("emailAddress") String emailAddress);
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)

    UserAccount findByUserId(int userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.role = :role WHERE u.userId = :userId")
    void updateRole(@Param("userId") int userId, @Param("role") Role role);

    UserAccount findByResetPasswordToken(String token);

    List<UserAccount> findByRole(Role role);
}
