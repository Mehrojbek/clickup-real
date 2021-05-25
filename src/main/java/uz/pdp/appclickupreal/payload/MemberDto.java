package uz.pdp.appclickupreal.payload;

import lombok.Data;
import uz.pdp.appclickupreal.entity.enums.Type;

import java.util.UUID;

@Data
public class MemberDto {
    private UUID userId;

    private UUID roleId;

    private Type type;
}
