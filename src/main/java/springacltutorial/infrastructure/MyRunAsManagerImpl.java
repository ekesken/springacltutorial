package springacltutorial.infrastructure;

import java.util.Collection;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.Authentication;

import springacltutorial.model.User;

public class MyRunAsManagerImpl extends RunAsManagerImpl {

	@Override
	public Authentication buildRunAs(Authentication authentication,
			Object object, Collection<ConfigAttribute> attributes) {
		if (this.getKey() == null) {
			return null;
		}
		boolean isSupported = false;
		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				isSupported = true;
			}
		}
		if (!isSupported) {
			return null;
		}
		ReflectiveMethodInvocation method = (ReflectiveMethodInvocation) object;
		Object[] arguments = method.getArguments();
		if (!(arguments[0] instanceof User)) {
			return null;
		}
		User user = (User) arguments[0];
		org.springframework.security.core.userdetails.User principal
				= new org.springframework.security.core.userdetails.User(
				user.getLogin(), "", true, true, true,
				true, user.getAuthorities());
		return new RunAsUserToken(this.getKey(), principal,
				null, user.getAuthorities(),
				authentication.getClass());
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
