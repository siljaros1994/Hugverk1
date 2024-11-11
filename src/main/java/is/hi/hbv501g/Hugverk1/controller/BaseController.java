package is.hi.hbv501g.Hugverk1.controller;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController {

    protected MyAppUsers getLoggedInUser(HttpSession session) {
        return (MyAppUsers) session.getAttribute("user");
    }

    protected boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}