package uz.pdp.appclickupreal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.pdp.appclickupreal.entity.enums.WorkspaceRoleName;
import uz.pdp.appclickupreal.entity.template.AbsUUIDEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id","name"}))
public class WorkspaceRole extends AbsUUIDEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Workspace workspace;

    private String name;

    @Enumerated(EnumType.STRING)
    private WorkspaceRoleName workspaceRoleName;
}
