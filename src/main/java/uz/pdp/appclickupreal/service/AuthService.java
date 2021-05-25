package uz.pdp.appclickupreal.service;

import uz.pdp.appclickupreal.payload.ApiResponse;
import uz.pdp.appclickupreal.payload.LoginDto;
import uz.pdp.appclickupreal.payload.RegisterDto;

public interface AuthService {

    ApiResponse register(RegisterDto registerDto);

    ApiResponse login(LoginDto loginDto);
}
