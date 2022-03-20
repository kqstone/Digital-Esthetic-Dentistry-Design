package tk.kqstone.dedd;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

final class BasicInfo implements Serializable {
	private static final String NAME = "name";
	private static final String AGE = "age";
	private static final String GENDER = "gender";
	private static final String PHONE = "phone";
	protected String name;
	protected int age;
	protected Gender gender;
	protected String phone;

	public BasicInfo(String name, int age, Gender gender, String phone) {
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}

	public BasicInfo() {
	}

	public Map<String, String> getHashMap() {
		Map<String, String> map = new HashMap<>();
		map.put(NAME, name);
		map.put(AGE, String.valueOf(age));
		map.put(GENDER, gender.value);
		map.put(PHONE, phone);
		return map;
	}

	public static BasicInfo getBasicInfo(Map<String, String> basicInfoMap) {
		BasicInfo info = new BasicInfo();
		info.name = basicInfoMap.get(NAME);
		switch (basicInfoMap.get(GENDER)) {
		case Constant.MALE:
			info.gender = Gender.MALE;
			break;
		case Constant.FEMALE:
			info.gender = Gender.FEMALE;
			break;
		default:
			info.gender = Gender.UNKNOW;
			break;
		}
		info.age = Integer.parseInt(basicInfoMap.get(AGE));
		info.phone = basicInfoMap.get(PHONE);
		return info;
	}

}
