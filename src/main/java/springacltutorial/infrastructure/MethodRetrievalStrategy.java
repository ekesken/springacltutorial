package springacltutorial.infrastructure;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;

public class MethodRetrievalStrategy implements
		ObjectIdentityRetrievalStrategy {

	public ObjectIdentity getObjectIdentity(Object domainObject) {
		MethodInvocation method = (MethodInvocation) domainObject;
		return new ObjectIdentityImpl(MethodInvocation.class, method.getMethod().toString());
	}
}