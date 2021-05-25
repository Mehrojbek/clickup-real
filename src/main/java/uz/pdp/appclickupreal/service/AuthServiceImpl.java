package uz.pdp.appclickupreal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.pdp.appclickupreal.component.UseFullMethods;
import uz.pdp.appclickupreal.entity.User;
import uz.pdp.appclickupreal.entity.enums.SystemRoleName;
import uz.pdp.appclickupreal.payload.ApiResponse;
import uz.pdp.appclickupreal.payload.LoginDto;
import uz.pdp.appclickupreal.payload.RegisterDto;
import uz.pdp.appclickupreal.repository.UserRepository;
import uz.pdp.appclickupreal.security.JwtProvider;

import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService,UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UseFullMethods useFullMethods;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }


    @Override
    public ApiResponse register(RegisterDto registerDto) {
        boolean exists = userRepository.existsByEmail(registerDto.getEmail());
        if (exists)
            return new ApiResponse("This email allready exist",false);

        User user = new User(
                registerDto.getFullName(),
                registerDto.getEmail(),
                registerDto.getPassword(),
                SystemRoleName.ROLE_USER
        );


        int code = new Random().nextInt(999999);
        user.setEmailCode(String.valueOf(code).substring(0,4));

        userRepository.save(user);

        useFullMethods.sendEmail(user.getEmail(), user.getEmailCode());

        return new ApiResponse("success",true);
    }


    public ApiResponse login(LoginDto loginDto){
        try {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            String token = jwtProvider.generateToken(loginDto.getUsername());
            return new ApiResponse("ok",true,token);
        }catch (Exception e){
            return new ApiResponse("login or password wrong",false);
        }
    }




}
