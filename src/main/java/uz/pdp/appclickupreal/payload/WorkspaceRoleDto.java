package uz.pdp.appclickupreal.payload;

import lombok.Data;
import uz.pdp.appclickupreal.entity.enums.WorkspacePermissionName;
import uz.pdp.appclickupreal.entity.enums.WorkspaceRoleName;

import javax.validation.constraints.NotNull;
import java.util.*;

@Data
public class WorkspaceRoleDto {

    @NotNull
    private String name;

    @NotNull
    private WorkspaceRoleName workspaceRoleName;

    private List<WorkspacePermissionName> workspacePermissionNames;
}
