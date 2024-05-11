package link.signalapp.service;

import link.signalapp.error.exception.SignalAppUnauthorizedException;
import link.signalapp.model.User;
import link.signalapp.security.SignalAppUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ServiceBase {

    protected User getUserFromContext() {
        return getUserDetailsFromContext().getUser();
    }

    protected SignalAppUserDetails getUserDetailsFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof SignalAppUserDetails)) {
            throw new SignalAppUnauthorizedException();
        }
        return (SignalAppUserDetails) principal;
    }

}
