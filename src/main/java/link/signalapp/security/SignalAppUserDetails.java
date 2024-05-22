package link.signalapp.security;

import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class SignalAppUserDetails implements UserDetails {

    private final UserToken userToken;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;

    public SignalAppUserDetails(UserToken userToken) {
        this.userToken = userToken;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        Set<Role> roles = userToken.getId().getUser().getRoles();
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getPassword() {
        return userToken.getId().getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return userToken.getId().getUser().getEmail();
    }

    public User getUser() {
        return userToken.getId().getUser();
    }

    public String getToken() {
        return userToken.getId().getToken();
    }
}
