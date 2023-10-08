/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.ReplyComment;
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
public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Integer> {

    @Query("SELECT c FROM ReplyComment c WHERE c.commentId = :commentId")
    List<ReplyComment> getAllReplyCommentByCommentId(@Param("commentId") int commentId);

    @Modifying
    @Query("DELETE FROM ReplyComment c WHERE c.id = :replyCommentId AND c.userId = :userId")
    int deleteReplyComment(@Param("replyCommentId") int replyCommentId, @Param("userId") int userId);

    @Modifying
    @Query("DELETE FROM ReplyComment c WHERE c.commentId = :commentId")
    void deleteReplyCommentByCommentId(@Param("commentId") int commentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
    int deleteCommentByAdmin(@Param("commentId") int commentId);
}
