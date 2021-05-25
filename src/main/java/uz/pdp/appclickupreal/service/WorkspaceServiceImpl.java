package uz.pdp.appclickupreal.service;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import uz.pdp.appclickupreal.component.UseFullMethods;
import uz.pdp.appclickupreal.entity.*;
import uz.pdp.appclickupreal.entity.enums.WorkspacePermissionName;
import uz.pdp.appclickupreal.entity.enums.WorkspaceRoleName;
import uz.pdp.appclickupreal.payload.ApiResponse;
import uz.pdp.appclickupreal.payload.MemberDto;
import uz.pdp.appclickupreal.payload.WorkspaceDto;
import uz.pdp.appclickupreal.payload.WorkspaceRoleDto;
import uz.pdp.appclickupreal.repository.*;

import java.sql.Timestamp;
import java.util.*;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    final
    WorkspaceRepository workspaceRepository;
    final
    AttachmentRepository attachmentRepository;
    final
    WorkspaceRoleRepository workspaceRoleRepository;
    final
    WorkspacePermissionRepository workspacePermissionRepository;
    final
    WorkspaceUserRepository workspaceUserRepository;
    final
    UserRepository userRepository;
    final
    UseFullMethods useFullMethods;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository, AttachmentRepository attachmentRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspacePermissionRepository workspacePermissionRepository, WorkspaceUserRepository workspaceUserRepository, UserRepository userRepository, UseFullMethods useFullMethods) {
        this.workspaceRepository = workspaceRepository;
        this.attachmentRepository = attachmentRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceUserRepository = workspaceUserRepository;
        this.userRepository = userRepository;
        this.useFullMethods = useFullMethods;
    }



    /**
     * GET ALL WORKSPACES
     * @param user
     * @return ApiResponse
     */
    @Override
    public ApiResponse getAllWorkspaces(User user) {
        List<Workspace> workspaces = workspaceRepository.findAllByCreatedById(user.getId());
        return new ApiResponse("ok",true,workspaces);
    }


    /**
     * GET ALL MEMBERS
     * @param id
     * @param user
     * @return ApiResponse
     */
    @Override
    public ApiResponse getMembers(Long id, User user) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace noy found",false);
        Workspace workspace = optionalWorkspace.get();
        if (workspace.getCreatedBy().equals(user)) {
            List<User> users = userRepository.getAllMemberByWorkspaceIdAndRoleName(id, WorkspaceRoleName.ROLE_MEMBER.name());
            return new ApiResponse("ok",true,users);
        }
        return new ApiResponse("Error",false);
    }


    /**
     * GET ALL GUESTS
     * @param id
     * @param user
     * @return ApiResponse
     */
    @Override
    public ApiResponse getGuests(Long id, User user) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace noy found",false);
        Workspace workspace = optionalWorkspace.get();
        if (workspace.getCreatedBy().equals(user)) {
            List<User> users = userRepository.getAllMemberByWorkspaceIdAndRoleName(id, WorkspaceRoleName.ROLE_GUEST.name());
            return new ApiResponse("ok",true,users);
        }
        return new ApiResponse("Error",false);
    }


    /**
     * ADD WORKSPACE
     * @param workspaceDto
     * @param user
     * @return ApiResponse
     */
    @Override
    public ApiResponse add(WorkspaceDto workspaceDto, User user) {
        boolean exists = workspaceRepository.existsByNameAndOwnerId(workspaceDto.getName(), user.getId());
        if (exists)
            return new ApiResponse("This workspace already exist", false);

        Workspace workspace = new Workspace(
                workspaceDto.getName(),
                workspaceDto.getColor(),
                user,
                workspaceDto.getAvatarId() == null ? null : attachmentRepository.findById(workspaceDto.getAvatarId()).orElseThrow(() -> new ResourceNotFoundException("attachment"))
        );

        Workspace savedWorkspace = workspaceRepository.save(workspace);


        WorkspaceRole ownerRole = workspaceRoleRepository.save(new WorkspaceRole(savedWorkspace, WorkspaceRoleName.ROLE_OWNER.name(), null));
        WorkspaceRole adminRole = workspaceRoleRepository.save(new WorkspaceRole(savedWorkspace, WorkspaceRoleName.ROLE_ADMIN.name(), null));
        WorkspaceRole memberRole = workspaceRoleRepository.save(new WorkspaceRole(savedWorkspace, WorkspaceRoleName.ROLE_MEMBER.name(), null));
        WorkspaceRole guestRole = workspaceRoleRepository.save(new WorkspaceRole(savedWorkspace, WorkspaceRoleName.ROLE_GUEST.name(), null));


        WorkspacePermissionName[] workspacePermissionNames = WorkspacePermissionName.values();

        //WORKSPACEDA PERMISSIONLAR YARATISH
        List<WorkspacePermission> workspacePermissions = new ArrayList<>();
        for (WorkspacePermissionName workspacePermissionName : workspacePermissionNames) {

                 workspacePermissions.add(new WorkspacePermission(
                    ownerRole,
                    workspacePermissionName
                ));

            if (workspacePermissionName.getWorkspaceRoleNames().contains(WorkspaceRoleName.ROLE_ADMIN)) {
                workspacePermissions.add(new WorkspacePermission(
                        adminRole,
                        workspacePermissionName
                ));
            }

            if (workspacePermissionName.getWorkspaceRoleNames().contains(WorkspaceRoleName.ROLE_MEMBER)) {
                workspacePermissions.add(new WorkspacePermission(
                        memberRole,
                        workspacePermissionName
                ));
            }

            if (workspacePermissionName.getWorkspaceRoleNames().contains(WorkspaceRoleName.ROLE_GUEST)) {
                workspacePermissions.add(new WorkspacePermission(
                        guestRole,
                        workspacePermissionName
                ));
            }
        }
        //WORKSPACEDA PERMISSIONLAR YARATISH
        workspacePermissionRepository.saveAll(workspacePermissions);


        //OWNER GA HUQUQLAR BIRIKTIRISH
        workspaceUserRepository.save(new WorkspaceUser(
                workspace,
                user,
                ownerRole,
                null,
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis())
        ));

        return new ApiResponse("Ishxona yaratildi", true);
    }


    /**
     * ADD ROLE TO WORKSPACE
     * @param id
     * @param workspaceRoleDto
     * @return ApiResponse
     */
    @Override
    public ApiResponse addRole(Long id, WorkspaceRoleDto workspaceRoleDto, User user) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace not found",false);

        if (workspaceRoleDto.getWorkspaceRoleName().equals(WorkspaceRoleName.ROLE_MEMBER)
            || workspaceRoleDto.getWorkspaceRoleName().equals(WorkspaceRoleName.ROLE_GUEST)
            || workspaceRoleDto.getWorkspaceRoleName().equals(WorkspaceRoleName.ROLE_ADMIN)) {

            Workspace workspace = optionalWorkspace.get();
            if (workspace.getCreatedBy().equals(user)) {
                WorkspaceRole workspaceRole = new WorkspaceRole(
                        workspace,
                        workspaceRoleDto.getName(),
                        workspaceRoleDto.getWorkspaceRoleName()
                );

                workspaceRoleRepository.save(workspaceRole);

                List<WorkspacePermission> permissions = new ArrayList<>();

                for (WorkspacePermissionName workspacePermissionName : workspaceRoleDto.getWorkspacePermissionNames()) {
                    permissions.add(new WorkspacePermission(
                            workspaceRole,
                            workspacePermissionName
                    ));
                }

                workspacePermissionRepository.saveAll(permissions);

                return new ApiResponse("success", true);
            }
        }
        return new ApiResponse("Error",false);
    }


    /**
     * EDIT ROLE
     * @param id
     * @param workspaceRoleDto
     * @param user
     * @return
     */
    @Override
    public ApiResponse editRole(Long id, WorkspaceRoleDto workspaceRoleDto, User user) {

        Optional<WorkspaceRole> optionalWorkspaceRole = workspaceRoleRepository.findByWorkspaceIdAndName(id, workspaceRoleDto.getName());
        if (!optionalWorkspaceRole.isPresent())
            return new ApiResponse("role not found",false);

        WorkspaceRole workspaceRole = optionalWorkspaceRole.get();
        Workspace workspace = workspaceRepository.getById(id);
        //if (workspace.getOwner())
        return null;
    }

    /**
     * DELETE WORKSPACE
     * @param id
     * @param owner
     * @return ApiResponse
     */
    @Override
    public ApiResponse delete(Long id, User owner) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace not found", false);
        Workspace workspace = optionalWorkspace.get();
        try {
            if (workspace.getCreatedBy().equals(owner))
                workspaceRepository.deleteById(id);
            return new ApiResponse("deleted", true);
        } catch (Exception e) {
            return new ApiResponse("was not deleted", false);
        }
    }


    /**
     * EDIT WORKSPACE
     * @param id
     * @param workspaceDto
     * @return ApiResponse
     */
    @Override
    public ApiResponse editWorkspace(Long id, WorkspaceDto workspaceDto, User user) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace not found", false);

        Workspace workspace = optionalWorkspace.get();

        //SHU USER WORKSPACE NI EGASIMI
        if (workspace.getCreatedBy().equals(user)) {
            if (workspaceDto.getAvatarId() != null)
                workspace.setAvatar(attachmentRepository.findById(workspaceDto.getAvatarId()).orElseThrow(() -> new ResourceNotFoundException("attachment")));

            workspace.setColor(workspaceDto.getColor());
            workspace.setName(workspaceDto.getName());
            workspaceRepository.save(workspace);

            return new ApiResponse("workspace edited", true);
        }
        return new ApiResponse("Error", false);
    }


    /**
     * CHEANGE OWNER
     * @param id
     * @param ownerId
     * @param owner
     * @return ApiResponse
     */
    @Override
    public ApiResponse changeOwnerWorkspace(Long id, UUID ownerId, User owner) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);

        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace not found", false);

        Workspace workspace = optionalWorkspace.get();

        if (workspace.getCreatedBy().equals(owner)) {
            Optional<User> optionalUser = userRepository.findById(ownerId);
            if (!optionalUser.isPresent())
                return new ApiResponse("user not found", false);
            User user = optionalUser.get();
            workspace.setOwner(user);

            workspaceRepository.save(workspace);
            return new ApiResponse("owner changed", true);
        }
        return new ApiResponse("Error", false);
    }


    /**
     * ADD OR EDIT OR REMOVE USER TO WORKSPACE
     * @param id
     * @param memberDto
     * @return ApiResponse
     */
    @Override
    public ApiResponse addOrEditOrRemoveWorkspace(Long id, MemberDto memberDto, User owner) {

        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
        if (!optionalWorkspace.isPresent())
            return new ApiResponse("workspace not found",false);

        Workspace workspace = optionalWorkspace.get();
        if (workspace.getCreatedBy().equals(owner)) {
            switch (memberDto.getType()) {
                case ADD:
                    User user = userRepository.findById(memberDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("id"));
                    WorkspaceUser workspaceUser = new WorkspaceUser(
                            workspace,
                            user,
                            workspaceRoleRepository.findById(memberDto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("id")),
                            UUID.randomUUID().toString(),
                            new Timestamp(System.currentTimeMillis()),
                            null
                            );

                    workspaceUserRepository.save(workspaceUser);
                    String message = "Sizni "+workspace.getName()+" ishxonasiga taklif qilishdi \n" +
                            "http://localhost:8080/api/workspace/verify?joinedCode="+workspaceUser.getJoinedCode()+"&id="+workspace.getId();
                    useFullMethods.sendEmail(user.getEmail(),message);
                    break;
                case EDIT:
                    WorkspaceUser workspaceUser1 = workspaceUserRepository.findByWorkspaceIdAndUserId(workspace.getId(), memberDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("id"));
                    workspaceUser1.setWorkspaceRole(workspaceRoleRepository.findById(memberDto.getRoleId()).orElseThrow(() -> new ResourceNotFoundException("id")));
                    workspaceUserRepository.save(workspaceUser1);
                    break;
                case DELETE:
                    workspaceUserRepository.deleteById(memberDto.getUserId());
                    break;
            }
            return new ApiResponse("success",true);
        }
        return new ApiResponse("Error",false);
    }


    /**
     * JOIN TO WORKSPACE
     * @param id
     * @param user
     * @return ApiResponse
     */
    @Override
    public ApiResponse joinToWorkspace(Long id, User user, String joinedCode) {
        Optional<WorkspaceUser> optionalWorkspaceUser = workspaceUserRepository.findByWorkspaceIdAndUserId(id, user.getId());
        if (!optionalWorkspaceUser.isPresent())
            return new ApiResponse("not found",false);
        WorkspaceUser workspaceUser = optionalWorkspaceUser.get();
        workspaceUser.setJoinedCode(null);
        workspaceUser.setDateJoined(new Timestamp(System.currentTimeMillis()));
        workspaceUserRepository.save(workspaceUser);
        return new ApiResponse("successfully joined",true);
    }
}
