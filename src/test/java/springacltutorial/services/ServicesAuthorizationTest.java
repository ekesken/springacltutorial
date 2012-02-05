package springacltutorial.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import springacltutorial.dao.ReportsDao;
import springacltutorial.model.Record;
import springacltutorial.model.Report;
import springacltutorial.model.User;

@SuppressWarnings("deprecation")
public class ServicesAuthorizationTest {

	@Before
	public void setup() {
		SecurityContextHolder.getContext().setAuthentication(null);
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-business.xml",
						"applicationContext-security.xml" });
		recordServices = (RecordServices) BeanFactoryUtils.beanOfType(context,
				RecordServices.class);
		reportServices = (ReportServices) BeanFactoryUtils.beanOfType(context,
				ReportServices.class);
		dao = (ReportsDao) BeanFactoryUtils.beanOfType(context,
				ReportsDao.class);
	}

	RecordServices recordServices;
	ReportServices reportServices;
	ReportsDao dao;

	@Test
	public void testAddReport() {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl1", "pass1"));
		reportServices.addReport("springacltutorial");
		// now use user without EMPLOYEE role
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("testUser", ""));
		try {
			reportServices.addReport("springacltutorial");
			fail("should throw AccessDeniedException");
		} catch (BadCredentialsException e) {
			return; // ok
		}
	}

	@Test
	public void testAcceptReport() {
		// empl1 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl1", "pass1"));
		Report reportEmpl1 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		// empl3 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl3", "pass3"));
		Report reportEmpl3 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		// manager1 accepts report of empl1 - ok
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("manager1", "pass1"));
		assertEquals(false, reportEmpl1.isAccepted());
		reportServices.acceptReport(reportEmpl1);
		assertEquals(true, reportEmpl1.isAccepted());

		// manager1 tries to accept report of empl3 - access denied
		assertEquals(false, reportEmpl3.isAccepted());
		try {
			reportServices.acceptReport(reportEmpl3);
			fail("manager1 cannot accept reports of empl3");
		} catch (AccessDeniedException e) {
			// ok
		}
		assertEquals(false, reportEmpl3.isAccepted());

		// manager2 accepts report of empl3 - ok
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("manager2", "pass2"));
		reportServices.acceptReport(reportEmpl3);
		assertEquals(true, reportEmpl3.isAccepted());
	}

	@Test
	public void testGetReports() {
		// empl1 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl1", "pass1"));
		Report reportEmpl1 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		// empl3 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl3", "pass3"));
		Report reportEmpl3 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("manager1", "pass1"));
		List<Report> reports = reportServices.getReports();
		assertNotNull(reports);
		assertEquals(1, reports.size());
		assertEquals(reportEmpl1, reports.get(0));

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("manager2", "pass2"));
		reports = reportServices.getReports();
		assertNotNull(reports);
		assertEquals(1, reports.size());
		assertEquals(reportEmpl3, reports.get(0));
	}

	@Test
	public void testUpdateReport() {
		// empl1 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl1", "pass1"));
		Report reportEmpl1 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		reportServices.updateReport(reportEmpl1); // verifies no exception is thrown

		// empl3 creates report
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl3", "pass3"));
		Report reportEmpl3 = dao.getReportById(reportServices
				.addReport("springacltutorial"));

		reportServices.updateReport(reportEmpl3); // verifies no exception is thrown
		try {
			reportServices.updateReport(reportEmpl1);
			fail("access denied exception is expected");
		} catch (Exception e) {
			assertEquals(e.getClass(), AccessDeniedException.class);
		}

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("empl1", "pass1"));
		reportServices.updateReport(reportEmpl1); // verifies no exception is thrown
		try {
			reportServices.updateReport(reportEmpl3);
			fail("access denied exception is expected");
		} catch (Exception e) {
			assertEquals(e.getClass(), AccessDeniedException.class);
		}
	}

	@Test
	public void testCreateGetRecord() {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("consumer", "consumer"));
		User manager1 = new User("manager1");
		GrantedAuthority roleManager = new GrantedAuthorityImpl("ROLE_MANAGER");
		manager1.getAuthorities().add(roleManager);
		User user1 = new User("empl1");
		GrantedAuthority roleEmployee = new GrantedAuthorityImpl("ROLE_EMPLOYEE");
		user1.getAuthorities().add(roleEmployee);
		Long id = recordServices.createRecord(manager1, "springacltutorial");
		Record record = recordServices.getRecord(user1, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
		record = recordServices.getRecord(user1, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
		try {
			recordServices.createRecord(user1, "springacltutorial");
			fail("access denied exception is expected");
		} catch (Exception e) {
			assertEquals(e.getClass(), AccessDeniedException.class);
		}
	}
}