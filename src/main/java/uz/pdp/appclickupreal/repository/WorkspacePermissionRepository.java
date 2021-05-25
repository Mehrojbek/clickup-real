package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appclickupreal.entity.WorkspacePermission;

import java.util.UUID;

public interface WorkspacePermissionRepository extends JpaRepository<WorkspacePermission, UUID> {
}
