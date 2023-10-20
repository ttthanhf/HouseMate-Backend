/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.serviceId = :serviceId")
    List<Comment> getAllCommentByServiceId(@Param("serviceId") int serviceId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId AND c.userId = :userId")
    int deleteCommentByUser(@Param("commentId") int commentId, @Param("userId") int userId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
    int deleteCommentByAdmin(@Param("commentId") int commentId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId")
    Comment getCommentById(@Param("commentId") int commentId);
    
    
	@Query("SELECT COUNT(DISTINCT c.commentId) + COUNT(rc.commentId) FROM Comment c LEFT JOIN ReplyComment rc ON c.commentId = rc.commentId WHERE c.serviceId = :serviceId")
    int getAllCommentAndReplyByServiceId(@Param("serviceId") int serviceId);
}
