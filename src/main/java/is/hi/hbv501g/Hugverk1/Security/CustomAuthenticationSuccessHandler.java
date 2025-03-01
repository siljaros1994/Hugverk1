package is.hi.hbv501g.Hugverk1.Security;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.MyAppUsers;
import is.hi.hbv501g.Hugverk1.Services.MyAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private MyAppUserService myAppUserService;

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    // Redirect the user after successful login based on user type.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Here we get the username of the authenticated user
        String username = authentication.getName();
        logger.info("User authenticated successfully. Username: {}", username);

        // Here we fetch user details from the database
        MyAppUsers user = myAppUserService.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        // Store the user in the session
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("LoggedInUser", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userType", user.getUserType());

        // Log session attributes
        session.getAttributeNames().asIterator().forEachRemaining(attributeName -> {
            logger.info("Session Attribute - {}: {}", attributeName, session.getAttribute(attributeName));
        });

        // Here we set recipientId or donorId based on the user type
        if ("recipient".equalsIgnoreCase(user.getUserType()) && user.getRecipientId() != null) {
            session.setAttribute("recipientId", user.getRecipientId());
            logger.info("Recipient ID set in session: {}", user.getRecipientId());
        } else if ("donor".equalsIgnoreCase(user.getUserType()) && user.getDonorId() != null) {
            session.setAttribute("donorId", user.getDonorId());
            logger.info("Donor ID set in session: {}", user.getDonorId());
        } else if ("admin".equalsIgnoreCase(user.getUserType()) && user.getDonorId() != null) {
            session.setAttribute("adminId", user.getDonorId());
            logger.info("Admin ID set in session: {}", user.getId());
        }

        logger.info("Session user: {}", session.getAttribute("user"));
        logger.info("Session donorId: {}", session.getAttribute("donorId"));
        logger.info("Session recipientId: {}", session.getAttribute("recipientId"));

        // Determine the redirect URL based on user type
        String redirectUrl = switch (user.getUserType().toLowerCase()) {
            case "donor" -> "/home/donor";
            case "recipient" -> "/home/recipient";
            case "admin" -> "/home/admin";
            default -> "/users/login?error=true";
        };

        logger.info("Redirecting user to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}