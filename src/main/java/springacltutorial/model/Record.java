package springacltutorial.model;

public class Record {

	public static Long SEQUENCE = 0L;

	public Record(String name) {
		super();
		this.id = ++SEQUENCE;
		this.name = name;
	}

	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
