package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.SecurityRealm;
import hudson.tasks.MailAddressResolver;
import jenkins.model.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This implementation is used to determine build username variables from <b>{@link UserIdCause}</b>.
 *
 * @author GKonovalenko
 */
public class UserIdCauseDeterminant implements IUsernameSettable<UserIdCause> {

    private static final Logger log = Logger.getLogger(UserIdCauseDeterminant.class.getName());

    /**
     * {@inheritDoc}
     * <p>
     * <b>{@link UserIdCause}</b> based implementation.
     */
    public boolean setJenkinsUserBuildVars(UserIdCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        String username = cause.getUserName();
        UsernameUtils.setUsernameVars(username, variables);

        String trimmedUserId = StringUtils.trimToEmpty(cause.getUserId());
        String originalUserId = trimmedUserId.isEmpty() ? ACL.ANONYMOUS_USERNAME : trimmedUserId;
        String userid = mapUserId(originalUserId);

        variables.put(BuildUserVariable.ID, userid);

        setUserGroups(originalUserId, variables);
        setUserEmail(originalUserId, variables);

        return true;
    }

    private String mapUserId(String userId) {
        try {
            SecurityRealm realm = Jenkins.get().getSecurityRealm();
            if (realm instanceof SamlSecurityRealm samlSecurityRealm) {
                String conversion = samlSecurityRealm.getUsernameCaseConversion();
                return switch (conversion) {
                    case "lowercase" -> userId.toLowerCase();
                    case "uppercase" -> userId.toUpperCase();
                    default -> userId;
                };
            }
        } catch (NoClassDefFoundError e) {
            log.fine("It seems the saml plugin is not installed, skipping saml user name mapping.");
        }
        return userId;
    }

    private void setUserGroups(String userId, Map<String, String> variables) {
        try {
            Optional.ofNullable(User.getById(userId, false))
                    .map(User::impersonate2)
                    .map(authentication -> authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .filter(authority -> authority != null && !authority.isEmpty())
                                    .collect(Collectors.joining(","))
                    ).ifPresentOrElse(groups ->
						variables.put(BuildUserVariable.GROUPS, groups)
                    , () ->
                        variables.put(BuildUserVariable.GROUPS, "")
                    );
        } catch (Exception err) {
            log.warning(String.format("Failed to get groups for user: %s error: %s ", userId, err));
        }
    }

	private void setUserEmail(String userId, Map<String, String> variables) {
		Optional.ofNullable(User.getById(userId, false))
				.map(MailAddressResolver::resolve)
				.ifPresent(email -> variables.put(BuildUserVariable.EMAIL, email));
	}

    /**
     * {@inheritDoc}
     */
    public Class<UserIdCause> getUsedCauseClass() {
        return UserIdCause.class;
    }
}
