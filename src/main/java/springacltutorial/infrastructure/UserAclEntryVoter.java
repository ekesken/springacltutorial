package springacltutorial.infrastructure;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.acls.AclEntryVoter;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.Permission;

public class UserAclEntryVoter extends AclEntryVoter {

	public UserAclEntryVoter(AclService aclService,
			String processConfigAttribute, Permission[] requirePermission) {
		super(aclService, processConfigAttribute, requirePermission);
	}

	@Override
	protected Object getDomainObjectInstance(MethodInvocation invocation) {
		return invocation;
	}

}
