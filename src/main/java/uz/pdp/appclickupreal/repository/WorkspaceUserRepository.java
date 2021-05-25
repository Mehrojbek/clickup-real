package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appclickupreal.entity.WorkspaceUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser, UUID> {
    Optional<WorkspaceUser> findByWorkspaceIdAndUserId(Long workspace_id, UUID user_id);
}
