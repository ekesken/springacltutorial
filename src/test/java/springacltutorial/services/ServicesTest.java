package springacltutorial.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import springacltutorial.dao.ReportsDao;

import static org.junit.Assert.*;

public class ServicesTest {

	@Before
	public void setup() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext-business.xml");
		reportServices = (ReportServices) BeanFactoryUtils.beanOfType(context,
				ReportServices.class);
		dao = (ReportsDao) BeanFactoryUtils.beanOfType(context,
				ReportsDao.class);
	}

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

}