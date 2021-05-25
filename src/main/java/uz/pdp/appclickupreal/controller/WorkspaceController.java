package uz.pdp.appclickupreal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appclickupreal.entity.User;
import uz.pdp.appclickupreal.payload.*;
import uz.pdp.appclickupreal.security.CurrentUser;
import uz.pdp.appclickupreal.service.WorkspaceService;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    @Autowired
    WorkspaceService workspaceService;


    @GetMapping("/getWorkspaces")
    public HttpEntity<?> getWorkspaces(@CurrentUser User user){
        ApiResponse apiResponse = workspaceService.getAllWorkspaces(user);
        return ResponseEntity.ok(apiResponse);
    }


    /**
     * GET ALL MEMBERS
     * @param user
     * @param id
     * @return
     */
    @GetMapping("/getMembers/{id}")
    public HttpEntity<?> getMembers(@CurrentUser User user,
                                    @PathVariable Long id) {
        ApiResponse apiResponse = workspaceService.getMembers(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:401).body(apiResponse);
    }


    /**
     * GET ALL GUESTS
     * @param user
     * @param id
     * @return
     */
    @GetMapping("/get/getGuests{id}")
    public HttpEntity<?> getGuests(@CurrentUser User user,
                                    @PathVariable Long id) {
        ApiResponse apiResponse = workspaceService.getGuests(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:401).body(apiResponse);
    }


    /**
     * ADD WORKSPACE
     * @param workspaceDto
     * @param user
     * @return
     */
    @PostMapping
    public HttpEntity<?> add(@RequestBody WorkspaceDto workspaceDto, @CurrentUser User user){
        ApiResponse apiResponse = workspaceService.add(workspaceDto, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    /**
     * ADD ROLE TO WORKSPACE
     * @param id
     * @param workspaceRoleDto
     * @param user
     * @return
     */
    @PostMapping("/addRole")
    public HttpEntity<?> addRole(@PathVariable Long id,
                                 @RequestBody WorkspaceRoleDto workspaceRoleDto,
                                 @CurrentUser User user){
        ApiResponse apiResponse = workspaceService.addRole(id, workspaceRoleDto, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    /**
     * DELETE WORKSPACE
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Long id,@CurrentUser User user){
        ApiResponse apiResponse = workspaceService.delete(id, user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    /**
     * ADD OR EDIT OR REMOVE USER FROM WORKSPACE
     * @param id
     * @param memberDto
     * @return
     */
    @PostMapping("/AddOrEditOrRemoveUser/{id}")
    public HttpEntity<?> addOrEditOrRemoveWorkspace(@PathVariable Long id,
                                                    @RequestBody MemberDto memberDto,
                                                    @CurrentUser User owner){
        ApiResponse apiResponse = workspaceService.addOrEditOrRemoveWorkspace(id, memberDto,owner);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }


    /**
     * EDIT WORKSPACE
     * @param id
     * @param workspaceDto
     * @return
     */
    @PutMapping("/{id}")
    public HttpEntity<?> edit(@PathVariable Long id,
                              @RequestBody WorkspaceDto workspaceDto,
                              @CurrentUser User user){
        ApiResponse apiResponse = workspaceService.editWorkspace(id,workspaceDto,user);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }



    /**
     * CHANGE OWNER
     * @param id
     * @param
     * @return
     */
    @PutMapping("/changeOwner/{id}")
    public HttpEntity<?> edit(@PathVariable Long id,
                              @RequestParam UUID ownerId,
                              @CurrentUser User owner){
        ApiResponse apiResponse = workspaceService.changeOwnerWorkspace(id,ownerId,owner);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }



    /**
     * JOIN TO WORKSPACE
     * @param id
     * @param user
     * @return
     */
    @GetMapping("/joinToWorkspace")
    public HttpEntity<?> joinToWorkspace(@RequestParam Long id,
                                         @RequestParam String joinedCode,
                                         @CurrentUser User user){
        ApiResponse apiResponse = workspaceService.joinToWorkspace(id, user, joinedCode);
        return ResponseEntity.status(apiResponse.isSuccess()?200:409).body(apiResponse);
    }

}
