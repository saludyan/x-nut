package nut.jpa.model;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class XAuditorAware implements AuditorAware<String> {
    @Override
    public String getCurrentAuditor() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx == null) {
            return null;
        }
        if (ctx.getAuthentication() == null) {
            return null;
        }
        if (ctx.getAuthentication().getPrincipal() == null) {
            return null;
        }
        if(ctx.getAuthentication().getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) ctx.getAuthentication().getPrincipal();
            if (userDetails!=null) {
                return userDetails.getUsername();
            } else {
                return null;
            }
        }else{
            return null;
        }


    }
}
