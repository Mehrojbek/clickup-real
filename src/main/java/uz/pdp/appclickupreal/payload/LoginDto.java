package uz.pdp.appclickupreal.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginDto {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    private String password;
}
