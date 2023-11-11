/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author ThanhF
 */
@Repository
public interface UserRepository extends JpaRepository<UserAccount, Integer> {

    UserAccount findByEmailAddress(String emailAddress);

    UserAccount findByUserId(int userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAccount u SET u.role = :role WHERE u.userId = :userId")
    void updateRole(@Param("userId") int userId, @Param("role") Role role);

    UserAccount findByResetPasswordToken(String token);

    List<UserAccount> findByRole(Role role);

    UserAccount findByAddress(String address);

    UserAccount findByIdentityCard(String identityCard);
}
