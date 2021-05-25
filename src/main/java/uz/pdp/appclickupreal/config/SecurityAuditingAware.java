package uz.pdp.appclickupreal.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.appclickupreal.entity.User;

import java.util.Optional;


public class SecurityAuditingAware implements AuditorAware<User> {
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication!=null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())){
            User user = (User) authentication.getPrincipal();
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
