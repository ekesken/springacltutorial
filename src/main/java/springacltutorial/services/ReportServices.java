package springacltutorial.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import springacltutorial.dao.ReportsDao;
import springacltutorial.model.Report;
import springacltutorial.model.User;

@Service
public class ReportServices {

	@Autowired
	ReportsDao dao;

	// this method should be accessible only to users with role EMPLOYEE
	@Secured("ROLE_EMPLOYEE")
	public long addReport(String description) {
		Report report = new Report();
		report.setDescription(description);
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		if (auth != null) {
			if (auth.getPrincipal() instanceof UserDetails) {
				report.setUser(new User(((UserDetails) auth.getPrincipal())
						.getUsername()));
			} else {
				report.setUser(new User(auth.getPrincipal().toString()));
			}
		}
		dao.saveReport(report);
		return report.getId();
	}

	// this method may be called only by user with role MANAGER, being manager
	// of user linked to the report
	@Secured({ "ROLE_MANAGER", "ACL_REPORT_ACCEPT" })
	public void acceptReport(Report report) {

		report.setAccepted(true);
	}

}
