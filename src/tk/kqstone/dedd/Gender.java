package tk.kqstone.dedd;

public enum Gender {
	MALE("男"), FEMALE("女"), UNKNOW("*");

	public final String value;

	Gender(String value) {
		this.value = value;
	}

}
