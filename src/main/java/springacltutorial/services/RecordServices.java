package springacltutorial.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import springacltutorial.model.Record;
import springacltutorial.model.User;

@Service
public class RecordServices {

	List<Record> records = new ArrayList<Record>();

	@Secured({"RUN_AS_USER", "ROLE_MANAGER"})
	public Long createRecord(User user, String name) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("creating record with auth-'" + auth + "'");
		Record newRecord = new Record(name);
		records.add(newRecord);
		return newRecord.getId();
	}

	@Secured("RUN_AS_USER")
	@PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_EMPLOYEE')")
	public Record getRecord(User user, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("getting record for id-'" + id + "' with auth-'" + auth + "'");
		Integer userRecordsLength = records.size();
		for (int i = 0; i < userRecordsLength; i++) {
			Record record = records.get(i);
			if (record.getId() == id) {
				return record;
			}
		}
		return null;
	}
}
