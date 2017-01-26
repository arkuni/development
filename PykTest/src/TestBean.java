
public class TestBean {
	private String name

	public String getName() {
		this.name = "¹Ú" + this.name;
		return name;
	}

	public void setName(String name) {
		if (name.equals("¾Æ¿µ")) this.name = "À±±â";
		else this.name = name;
	}
}
