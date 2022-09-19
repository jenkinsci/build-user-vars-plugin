package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;

import java.util.Collection;
import java.util.Map;

import hudson.security.SecurityRealm;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import hudson.security.ACL;
import hudson.tasks.Mailer;
import hudson.model.User;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.springframework.security.core.GrantedAuthority;

/**
 * This implementation is used to determine build username variables from <b>{@link UserIdCause}</b>.
 * This will let to get whole set of variables:
 * <ul>
 *   <li>{@link IUsernameSettable#BUILD_USER_ID}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_GROUPS}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_FIRST_NAME_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_LAST_NAME_VAR_NAME}</li>
 * </ul>
 *
 * @author GKonovalenko
 */
public class UserIdCauseDeterminant implements IUsernameSettable<UserIdCause> {

	final Class<UserIdCause> causeClass = UserIdCause.class;
	private static final Logger log = Logger.getLogger(UserIdCauseDeterminant.class.getName());


	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>{@link UserIdCause}</b> based implementation.
	 */
	public boolean setJenkinsUserBuildVars(UserIdCause cause,
			Map<String, String> variables) {
		if(null != cause) {
			String username = cause.getUserName();
			UsernameUtils.setUsernameVars(username, variables);

			String trimmedUserId = StringUtils.trimToEmpty(cause.getUserId());
			String originalUserid = trimmedUserId.isEmpty() ? ACL.ANONYMOUS_USERNAME : trimmedUserId;
			String userid = originalUserid;
			StringBuilder groupString = new StringBuilder();
			try {
				Jenkins jenkinsInstance = Jenkins.get();
				SecurityRealm realm = jenkinsInstance.getSecurityRealm();
				userid = mapUserId (userid, realm);
				Collection<? extends GrantedAuthority> authorities = realm.loadUserByUsername2(originalUserid).getAuthorities();
				for (GrantedAuthority authority : authorities) {
					String authorityString = authority.getAuthority();
					if (authorityString != null && authorityString.length() > 0) {
						groupString.append(authorityString).append(",");
					}
				}
				groupString.setLength(groupString.length() == 0 ? 0 : groupString.length() - 1);
			} catch (Exception err) {
				// Error
				log.warning(String.format("Failed to get groups for user: %s error: %s ", userid, err));
			}
			variables.put(BUILD_USER_ID, userid);
			variables.put(BUILD_USER_VAR_GROUPS, groupString.toString());


			User user = User.getById(originalUserid, false);
			if (null != user) {
				Mailer.UserProperty prop = user.getProperty(Mailer.UserProperty.class);
				if (null != prop) {
					String adrs = StringUtils.trimToEmpty(prop.getAddress());
					variables.put(BUILD_USER_EMAIL, adrs);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private String mapUserId(String userid, SecurityRealm realm) {
		try {
			if (realm instanceof SamlSecurityRealm) {
				String conversion = ((SamlSecurityRealm) realm).getUsernameCaseConversion();
				switch (conversion) {
				case "lowercase":
					userid = userid.toLowerCase();
					break;
				case "uppercase":
					userid = userid.toUpperCase();
					break;
				default:
				}
			}
		} catch (NoClassDefFoundError e) {
			log.fine("It seems the saml plugin is not installed, skipping saml user name mapping.");
		}
		return userid;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<UserIdCause> getUsedCauseClass() {
		return causeClass;
	}

}
