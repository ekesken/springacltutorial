package springacltutorial.infrastructure;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.domain.BasePermission;

/**
 * Adds ACCEPT permission to standard set of Spring Security permissions. Based
 * on BasePermission code.
 */
public class ExtendedPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	public static final Permission ACCEPT = new ExtendedPermission(1 << 5, 'a'); // 32

	private ExtendedPermission(int mask, char code) {
		super(mask, code);
	}
}