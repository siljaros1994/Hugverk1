package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public abstract class BaseController {

    protected MyAppUsers getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MyAppUsers) {
            return (MyAppUsers) authentication.getPrincipal();
        }
        // Fallback: retrieve the user from the HttpSession attribute "user"
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        if (session != null && session.getAttribute("user") instanceof MyAppUsers) {
            return (MyAppUsers) session.getAttribute("user");
        }
        return null;
    }

    protected boolean isUserType(String userType) {
        MyAppUsers loggedInUser = getLoggedInUser();
        return loggedInUser != null && userType.equalsIgnoreCase(loggedInUser.getUserType());
    }
}