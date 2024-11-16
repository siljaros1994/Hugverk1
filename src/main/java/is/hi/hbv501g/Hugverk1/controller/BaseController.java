package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected MyAppUsers getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (MyAppUsers) authentication.getPrincipal();
    }

    protected boolean isUserType(String userType) {
        MyAppUsers loggedInUser = getLoggedInUser();
        return loggedInUser != null && userType.equalsIgnoreCase(loggedInUser.getUserType());
    }
}