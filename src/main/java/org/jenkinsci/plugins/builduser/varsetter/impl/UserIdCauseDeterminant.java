package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.SecurityRealm;
import hudson.tasks.Mailer;
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

	static final Class<UserIdCause> causeClass = UserIdCause.class;

	private static final Logger log = Logger.getLogger(UserIdCauseDeterminant.class.getName());

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>{@link UserIdCause}</b> based implementation.
	 */
	public boolean setJenkinsUserBuildVars(UserIdCause cause, Map<String, String> variables) {
		if (null != cause) {
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
					if (authorityString != null && !authorityString.isEmpty()) {
						groupString.append(authorityString).append(",");
					}
				}
				groupString.setLength(groupString.length() == 0 ? 0 : groupString.length() - 1);
			} catch (Exception err) {
				log.warning(String.format("Failed to get groups for user: %s error: %s ", userid, err));
			}
			variables.put(BuildUserVariable.ID, userid);
			variables.put(BuildUserVariable.GROUPS, groupString.toString());

			User user = User.getById(originalUserid, false);
			if (null != user) {
				Mailer.UserProperty prop = user.getProperty(Mailer.UserProperty.class);
				if (null != prop) {
					String address = StringUtils.trimToEmpty(prop.getAddress());
					variables.put(BuildUserVariable.EMAIL, address);
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
