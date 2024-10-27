package is.hi.hbv501g.Hugverk1.Security;

import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private MyAppUserService myAppUserService;

    // Redirect the user after successful login based on user type.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Here we get the username of the authenticated user
        String username = authentication.getName();

        // Here we fetch users details from the database
        MyAppUsers user = myAppUserService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Store the user in the session
        request.getSession().setAttribute("LoggedInUser", user);
        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("userType", user.getUserType());

        // Redirect based on user type
        if ("donor".equalsIgnoreCase(user.getUserType())) {
            response.sendRedirect("/home/donor");
        } else if ("recipient".equalsIgnoreCase(user.getUserType())) {
            response.sendRedirect("/home/recipient");
        } else {
            response.sendRedirect("/users/login?error=true");
        }
    }
}