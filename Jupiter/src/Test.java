
public class Test {
	int a;
	
	public Test () {
		try {
			a = -1;
			throw(new Exception("for test"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("test for 1");
	}
	
	public static void main(String[] args) {
		Test t = new Test();
		System.out.println(t.a);
	}
}
