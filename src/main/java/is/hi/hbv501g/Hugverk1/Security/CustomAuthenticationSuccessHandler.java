package is.hi.hbv501g.Hugverk1.Security;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("LoggedInUser", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userType", user.getUserType());

        // Here we set recipientId or donorId based on the user type
        if ("recipient".equalsIgnoreCase(user.getUserType()) && user.getRecipientId() != null) {
            session.setAttribute("recipientId", user.getRecipientId());
            System.out.println("Recipient ID set in session: " + user.getRecipientId());
        } else if ("donor".equalsIgnoreCase(user.getUserType()) && user.getDonorId() != null) {
            session.setAttribute("donorId", user.getDonorId());
            System.out.println("Donor ID set in session: " + user.getDonorId());
        } else if ("admin".equalsIgnoreCase(user.getUserType()) && user.getDonorId() != null) {
            session.setAttribute("adminId", user.getDonorId());
            System.out.println("Admin ID set in session: " + user.getId());
        }

        System.out.println("Session user: " + session.getAttribute("user"));
        System.out.println("Session donorId: " + session.getAttribute("donorId"));
        System.out.println("Session recipientId: " + session.getAttribute("recipientId"));

        String redirectUrl = switch (user.getUserType().toLowerCase()) {
            case "donor" -> "/home/donor";
            case "recipient" -> "/home/recipient";
            case "admin" -> "/home/admin";
            default -> "/users/login?error=true";
        };
        response.sendRedirect(redirectUrl);
    }
}
