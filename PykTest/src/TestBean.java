
public class TestBean {
	private String name

	public String getName() {
		this.name = "��" + this.name;
		return name;
	}

	public void setName(String name) {
		if (name.equals("�ƿ�")) this.name = "����";
		else this.name = name;
	}
}
