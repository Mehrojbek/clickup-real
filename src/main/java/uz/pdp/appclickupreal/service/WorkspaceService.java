package uz.pdp.appclickupreal.service;

import uz.pdp.appclickupreal.entity.User;
import uz.pdp.appclickupreal.payload.ApiResponse;
import uz.pdp.appclickupreal.payload.MemberDto;
import uz.pdp.appclickupreal.payload.WorkspaceDto;
import uz.pdp.appclickupreal.payload.WorkspaceRoleDto;

import java.util.UUID;

public interface WorkspaceService {

    ApiResponse add(WorkspaceDto workspaceDto, User user);

    ApiResponse addRole(Long id, WorkspaceRoleDto workspaceRoleDto, User user);

    ApiResponse editRole(Long id, WorkspaceRoleDto workspaceRoleDto, User user);

    ApiResponse editWorkspace(Long id, WorkspaceDto workspaceDto,User user);

    ApiResponse changeOwnerWorkspace(Long id, UUID ownerId,User owner);

    ApiResponse delete(Long id, User owner);

    ApiResponse addOrEditOrRemoveWorkspace(Long id, MemberDto memberDto,User owner);

    ApiResponse joinToWorkspace(Long id, User user, String joinedCode);

    ApiResponse getMembers(Long id, User user);

    ApiResponse getGuests(Long id, User user);

    ApiResponse getAllWorkspaces(User user);
}
