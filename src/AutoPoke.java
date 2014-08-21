public class AutoPoke {
	public static void main(String args[]) {
		FacebookUtil util = FacebookUtil.getInstance();
		util.setId("kh4975@naver.com");
		util.setPw("");
		System.out.println(util.doLogin());
		util.getPokeUser();
		util.getMyData();
	}
}
