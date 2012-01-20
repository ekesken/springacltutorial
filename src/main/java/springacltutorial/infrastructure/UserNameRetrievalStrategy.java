package springacltutorial.infrastructure;

import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import springacltutorial.model.User;

/**
 * overwrite the strategy: build ObjectIdentity based on user object login
 * property, instead of Spring Security default getId() call
 */
public class UserNameRetrievalStrategy implements
		ObjectIdentityRetrievalStrategy {

	public ObjectIdentity getObjectIdentity(Object domainObject) {
		User user = (User) domainObject;
		return new ObjectIdentityImpl(User.class, user.getLogin());
	}

}