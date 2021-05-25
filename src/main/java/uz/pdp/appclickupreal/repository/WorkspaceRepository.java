package uz.pdp.appclickupreal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appclickupreal.entity.Workspace;

import java.util.*;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace,Long> {
    boolean existsByNameAndOwnerId(String name, UUID owner_id);
    List<Workspace> findAllByCreatedById(UUID createdBy_id);


}
