/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.ServiceComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface CommentRepository extends JpaRepository<ServiceComment, Integer> {

    @Query("SELECT c FROM ServiceComment c WHERE c.serviceId = :serviceId")
    List<ServiceComment> getAllCommentByServiceId(@Param("serviceId") int serviceId);

    ServiceComment save(ServiceComment serviceComment);

    void deleteById(ServiceComment serviceComment);
}