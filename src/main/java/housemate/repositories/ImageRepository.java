/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.constants.ImageType;
import housemate.entities.Image;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author ThanhF
 */
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Image findById(int id);

    Optional<List<Image>> findAllByEntityIdAndImageType(int entityId, ImageType imgType);

    List<Image> findAllByEntityIdAndImageTypeWithoutOptional(int entityId, ImageType imgType);

    Optional<Image> findFirstByEntityIdAndImageType(int entityId, ImageType imgType);
}
