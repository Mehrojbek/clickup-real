package uz.pdp.appclickupreal.payload;

import lombok.Data;
import uz.pdp.appclickupreal.entity.Attachment;
import uz.pdp.appclickupreal.entity.User;

import java.util.UUID;


@Data
public class WorkspaceDto {
    private String name;

    private String color;

    private User owner;

    private UUID avatarId;
}
