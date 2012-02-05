package springacltutorial.services;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import springacltutorial.dao.ReportsDao;
import springacltutorial.model.Record;
import springacltutorial.model.Report;
import springacltutorial.model.User;

import static org.junit.Assert.*;

public class ServicesTest {

	@Before
	public void setup() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext-business.xml");
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
		long id = reportServices.addReport("springacltutorial");
		assertEquals("springacltutorial", dao.getReportById(id)
				.getDescription());
	}

	@Test
	public void testAcceptReport() {
		long id = reportServices.addReport("springacltutorial");
		assertEquals(false, dao.getReportById(id).isAccepted());
		reportServices.acceptReport(dao.getReportById(id));
		assertEquals(true, dao.getReportById(id).isAccepted());
	}

	@Test
	public void testGetReports() {
		reportServices.addReport("springacltutorial");
		reportServices.addReport("springacltutorial2");
		reportServices.addReport("springacltutorial3");
		Collection<Report> reports = reportServices.getReports();
		assertNotNull(reports);
		assertEquals(3, reports.size());
	}

	@Test
	public void testUpdateReport() {
		long id = reportServices.addReport("springacltutorial");
		Report report = dao.getReportById(id);
		reportServices.updateReport(report); // verifies no authorization exception is throwed
	}

	@Test
	public void testCreateGetRecord() {
		User user = new User("empl1");
		Long id = recordServices.createRecord(user, "springacltutorial");
		Record record = recordServices.getRecord(user, id);
		assertEquals(id, record.getId());
		assertEquals("springacltutorial", record.getName());
	}
}