package ru.checkout.services;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkout.domain.User;
import ru.checkout.domain.UserRole;
import ru.checkout.utils.CustomUserDetails;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Inject
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = (User) sessionFactory.getCurrentSession().createCriteria(User.class)
                .add(Restrictions.like("userName", username)).uniqueResult();

        if (user == null) throw new UsernameNotFoundException(null);

        Collection<GrantedAuthority> authorities = buildUserAuthority(user.getUserRoles());
        return buildUserForAuthentication(user, authorities);
    }

    private CustomUserDetails buildUserForAuthentication(User user, Collection<GrantedAuthority> authorities) {
        return new CustomUserDetails(user.getUserName(), user.getPassword(),
                user.isEnabled(), true, true, true, authorities);
    }

    private Collection<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {

        Set<GrantedAuthority> auths = new HashSet();

        for (UserRole userRole : userRoles) {
            auths.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
        }

        return auths;
    }

}