package springacltutorial.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import springacltutorial.model.Record;
import springacltutorial.model.User;

@Service
public class RecordServices {

	Map<User, List<Record>> records = new HashMap<User, List<Record>>();

	public Long createRecord(User user, String name) {
		List<Record> userRecords = records.get(user);
		if (userRecords == null) {
			userRecords = new ArrayList<Record>();
		}
		Record newRecord = new Record(name);
		userRecords.add(newRecord);
		records.put(user, userRecords);
		return newRecord.getId();
	}

	public Record getRecord(User user, Long id) {
		List<Record> userRecords = records.get(user);
		if (userRecords == null) {
			return null;
		}
		Integer userRecordsLength = userRecords.size();
		for (int i = 0; i < userRecordsLength; i++) {
			Record record = userRecords.get(i);
			if (record.getId() == id) {
				return record;
			}
		}
		return null;
	}
}
