package is.hi.hbv501g.Hugverk1.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.GenericFilterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CustomFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log session information
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            logger.info("Session ID: {}", session.getId());
            session.getAttributeNames().asIterator().forEachRemaining(attributeName -> {
                logger.info("Session Attribute - {}: {}", attributeName, session.getAttribute(attributeName));
            });
        } else {
            logger.info("No session found.");
        }

        chain.doFilter(request, response);
    }
}