/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.UserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
public interface UserRepository extends JpaRepository<UserAccount, Integer> {

    @Query("SELECT u FROM UserAccount u WHERE u.email_address = :emailAddress")
    UserAccount findByEmailAddress(@Param("emailAddress") String emailAddress);

}
