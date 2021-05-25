package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appclickupreal.entity.WorkspaceRole;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRoleRepository extends JpaRepository<WorkspaceRole, UUID> {
    Optional<WorkspaceRole> findByWorkspaceIdAndName(Long workspace_id, String name);
}
