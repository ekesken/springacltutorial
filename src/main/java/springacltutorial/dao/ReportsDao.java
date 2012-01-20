package springacltutorial.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import springacltutorial.model.Report;

@Repository
public class ReportsDao {

	private long sequence;
	private Map<Long, Report> reports = new HashMap<Long, Report>();

	public void saveReport(Report report) {
		if (report.getId() == 0) {
			report.setId(++sequence);
		}
		reports.put(report.getId(), report);
	}

	public Report getReportById(long reportId) {
		return reports.get(reportId);
	}

}