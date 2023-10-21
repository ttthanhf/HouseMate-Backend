/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ThanhF
 */
public interface ImageRepository extends JpaRepository<Image, Integer> {

}
