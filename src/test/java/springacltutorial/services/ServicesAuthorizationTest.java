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
		Record.SEQUENCE = 0L;
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
		GrantedAuthority roleAdmin = new GrantedAuthorityImpl("ROLE_ADMIN");
		GrantedAuthority roleManager = new GrantedAuthorityImpl("ROLE_MANAGER");
		GrantedAuthority roleEmployee = new GrantedAuthorityImpl("ROLE_EMPLOYEE");
		User admin = new User("admin");
		admin.getAuthorities().add(roleAdmin);
		User manager1 = new User("manager1");
		manager1.getAuthorities().add(roleManager);
		User manager2 = new User("manager2");
		manager2.getAuthorities().add(roleManager);
		User user1 = new User("empl1");
		user1.getAuthorities().add(roleEmployee);
		// CONSUMER1 TESTS
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("consumer", "consumer"));
		// MANAGER1 USER TESTS
		Long id = recordServices.createRecord(manager1, "springacltutorial");
		Record record = recordServices.getRecord(manager1, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
		Collection<Record> records = recordServices.getRecords(manager1);
		assertNotNull(records);
		assertEquals(1, records.size());
		assertEquals(1L, (long) records.iterator().next().getId());
		// MANAGER2 USER TESTS
		id = recordServices.createRecord(manager2, "springacltutorial");
		record = recordServices.getRecord(manager2, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
		records = recordServices.getRecords(manager2);
		assertNotNull(records);
		assertEquals(1, records.size());
		assertEquals(2L, (long) records.iterator().next().getId());
		// USER1 USER TESTS
		record = recordServices.getRecord(user1, 1L);
		assertEquals(1L, (long) record.getId());
		assertEquals("springacltutorial", record.getName());
		record = recordServices.getRecord(user1, 2L);
		assertEquals(2L,(long) record.getId());
		assertEquals("springacltutorial", record.getName());
		records = recordServices.getRecords(user1);
		assertNotNull(records);
		assertEquals(0, records.size());
		try {
			recordServices.createRecord(user1, "springacltutorial");
			fail("access denied exception is expected");
		} catch (Exception e) {
			assertEquals(e.getClass(), AccessDeniedException.class);
		}
		// ADMIN USER TESTS
		id = recordServices.createRecord(admin, "springacltutorial");
		record = recordServices.getRecord(admin, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
		records = recordServices.getRecords(user1);
		assertNotNull(records);
		assertEquals(0, records.size());
		// CONSUMER2 TESTS
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("consumer2", "consumer2"));
		try {
			recordServices.createRecord(manager1, "springacltutorial");
			fail("access denied exception is expected");
		} catch (Exception e) {
			assertEquals(e.getClass(), AccessDeniedException.class);
		}
	}
}