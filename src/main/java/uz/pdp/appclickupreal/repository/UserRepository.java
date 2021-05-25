package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.appclickupreal.entity.User;

import java.util.*;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "select  * from users u join workspace_user wu on u.id=wu.user_id " +
            "join workspace_role wr on wr.id=wu.workspace_role_id where wr.workspace_id=:workspaceId and wr.name=:roleName",nativeQuery = true)
    List<User> getAllMemberByWorkspaceIdAndRoleName(@Param("workspaceId") Long workspaceId, @Param("roleName") String roleName);
}
