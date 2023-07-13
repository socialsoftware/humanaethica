package pt.ulisboa.tecnico.socialsoftware.humanaethica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Member;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository;

import java.io.Serializable;

@Component
public class HEPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        AuthUser authUser = ((AuthUser) authentication.getPrincipal());
        int userId = authUser.getUser().getId();

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "ACTIVITY.MEMBER":
                    Activity activity = activityRepository.findById(id).orElse(null);
                    if (activity == null) return false;
                    return activity.getInstitution().getId().equals(((Member)authUser.getUser()).getInstitution().getId());
                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
