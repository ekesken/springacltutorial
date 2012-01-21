package springacltutorial.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

	@PostFilter("hasPermission(filterObject, 'read')")
	public List<Report> getReports() {
		return dao.getReports();
	}

	@PreAuthorize("#report.user.login == authentication.name")
	public void updateReport(Report report) {
		// throws exception if not authorized
	}
}
