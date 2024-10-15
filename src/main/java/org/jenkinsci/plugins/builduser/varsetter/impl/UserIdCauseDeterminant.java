package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.SecurityRealm;
import hudson.tasks.Mailer.UserProperty;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

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
        variables.put(BuildUserVariable.GROUPS, getUserGroups(originalUserId));

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

	private String getUserGroups(String userId) {
		StringBuilder groupString = new StringBuilder();
		try {
			SecurityRealm realm = Jenkins.get().getSecurityRealm();
			Collection<? extends GrantedAuthority> authorities = realm.loadUserByUsername2(userId).getAuthorities();
			for (GrantedAuthority authority : authorities) {
				String authorityString = authority.getAuthority();
				if (authorityString != null && !authorityString.isEmpty()) {
					groupString.append(authorityString).append(",");
				}
			}
			if (!groupString.isEmpty()) {
				groupString.setLength(groupString.length() - 1); // Remove trailing comma
			}
		} catch (Exception err) {
			log.warning(String.format("Failed to get groups for user: %s error: %s ", userId, err));
		}
		return groupString.toString();
	}

	private void setUserEmail(String userId, Map<String, String> variables) {
		User user = User.getById(userId, false);
		if (user == null) {
			return;
		}

		UserProperty prop = user.getProperty(UserProperty.class);
		if (prop == null) {
			return;
		}

		String address = StringUtils.trimToEmpty(prop.getAddress());
		variables.put(BuildUserVariable.EMAIL, address);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<UserIdCause> getUsedCauseClass() {
		return UserIdCause.class;
	}
}
