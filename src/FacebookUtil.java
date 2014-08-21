import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("deprecation")
public class FacebookUtil {
	private static FacebookUtil instance;
	private FacebookUtil() {};
	private String FACEBOOK_ID = null;
	private String FACEBOOK_PW = null;
	private final String FACEBOOK_URL = "https://www.facebook.com";
	private final String FACEBOOK_LOGIN_URL = "https://www.facebook.com/login.php?login_attempt=1";
	private final String FACEBOOK_POKE_URL = "https://m.facebook.com/pokes";
	private final String FACEBOOK_PROFILE_URL = "https://m.facebook.com/profile.php";
	private DefaultHttpClient client;
	
	public synchronized static FacebookUtil getInstance() {
		if(instance == null) {
			instance = new FacebookUtil();
		}
		return instance;
	}
	
	public void setId(String id) {
		this.FACEBOOK_ID = id;
	}
	
	public void setPw(String pw) {
		this.FACEBOOK_PW = pw;
	}
	
	public void getPokeUser() {
		try {
			HttpGet pokehttp = new HttpGet(FACEBOOK_POKE_URL);
			HttpResponse response = this.client.execute(pokehttp);
			StringBuilder stringbuilder = new StringBuilder();
			InputStream stream = response.getEntity().getContent();
			Scanner scanner = new Scanner(stream);
			while(scanner.hasNextLine()) {
				stringbuilder.append(scanner.nextLine());
			}
			Document document = Jsoup.parse(stringbuilder.toString());
			/*
			 * 누가 나를 몇번 찔렀는지 또는 찌르기 추천 친구 (이름, 주소, 횟수)
			 */
			Elements poketimeelements = document.getElementsByClass("_5i1s");
			Elements pokelinkelements = document.getElementsByClass("_5mpk");
			Elements pokeprofileelements = document.getElementsByClass("ib");
			Elements pokeprofilemsgelements = document.getElementsByClass("_5hn8");
			System.out.println();
			for(int i=0; i<pokeprofileelements.size(); i++) {
				System.out.println(pokeprofilemsgelements.get(i).children().get(0).ownText());
				System.out.println(pokeprofilemsgelements.get(i).children().size());
				System.out.printf("이름 : ");
				System.out.println(pokeprofileelements.get(i).select("a").get(1).ownText()); // 친구 이름
				System.out.printf("주소 : ");
				System.out.println(pokeprofileelements.get(i).select("a").get(1).attr("href")); // 친구 페북 주소
				System.out.printf("시간 : ");
				if(poketimeelements.get(i).ownText().isEmpty()) {
					System.out.println(poketimeelements.get(i).select("abbr").get(0).ownText());
				} else {
					System.out.println(poketimeelements.get(i).ownText());
				}
				// 누르면 찌르기가 됨
				System.out.printf("찌르기링크 : ");
				System.out.println(pokelinkelements.get(i).select("a[href]").get(0).attr("href"));
				System.out.printf("찌르기메세지 : ");
				System.out.println(pokelinkelements.get(i).getElementsByClass("_55sr").get(0).ownText());
				
				// 누르면 숨김이 됨
				System.out.printf("숨기기링크 : ");
				System.out.println(pokelinkelements.get(i).select("a[href]").get(1).attr("href"));
				System.out.printf("찌르기맨트 : ");
				System.out.println(pokelinkelements.get(i).select("a[href]").get(1).attr("aria-label"));
				
				// Profile Photo
				System.out.printf("프로필이미지링크 : ");
				System.out.println(pokeprofileelements.get(i).select("img").attr("src")); // 프로필 이미지
//				System.out.println(pokeprofileelements.get(i).select("a").get(2)); // 모든링크
				System.out.println();

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void resetClient() {
		this.client = null;
		this.client = new DefaultHttpClient();
	}
	
	public void getMyData() {
		try {
			HttpGet pokehttp = new HttpGet(FACEBOOK_POKE_URL);
			HttpResponse response = this.client.execute(pokehttp);
			StringBuilder stringbuilder = new StringBuilder();
			InputStream stream = response.getEntity().getContent();
			Scanner scanner = new Scanner(stream);
			while (scanner.hasNextLine()) {
				stringbuilder.append(scanner.nextLine());
			}
			Document document = Jsoup.parse(stringbuilder.toString());
			Element pokeelement = document.getElementById("poke_area");
			for(int i=0; i<pokeelement.childNodeSize(); i++) {
				Elements DataElements = pokeelement.child(i).select("a");
				Elements TimeElements = pokeelement.child(i).select("abbr");
				try {
					System.out.println(DataElements.get(0).select("img").attr("src")); // Profile Image
					System.out.println(DataElements.get(1).ownText());// Friend Name
					System.out.println(DataElements.get(2).attr("href")); //Poke Link
					System.out.println(DataElements.get(2).select("span").get(0).ownText());// Poke Message
					System.out.println(DataElements.get(1).parent().ownText()); // Poke Time
					System.out.println(DataElements.get(3).attr("href")); //Hide Link
					System.out.println(DataElements.get(3).attr("aria-label")); //Hide Message
					System.out.println(DataElements.get(1).attr("href")); //Facebook Profile Home
					try {
						System.out.println(TimeElements.get(0).ownText());
					} catch(IndexOutOfBoundsException e) {
						System.out.println(pokeelement.child(i).getAllElements().get(9).ownText());	
					}
				} catch(IndexOutOfBoundsException e) {}
				System.out.println("###########################");
			}
//			System.out.println(pokeelement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean doLogin() {
		Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);
		Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		Logger.getLogger("httpclient").setLevel(Level.OFF);
		boolean sessionSet = false;
		resetClient();
		if ((FACEBOOK_ID == null || (FACEBOOK_PW) == null)) {
			return false;
		}
		try {
			HttpGet initload = new HttpGet(FACEBOOK_URL);
			HttpPost login = new HttpPost(FACEBOOK_LOGIN_URL);

			HttpResponse response = this.client.execute(initload);
			HttpEntity entity = response.getEntity();
			if(entity != null) EntityUtils.consume(entity);
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("lsd",""));
			parameters.add(new BasicNameValuePair("email",this.FACEBOOK_ID));
			parameters.add(new BasicNameValuePair("pass",this.FACEBOOK_PW));
			parameters.add(new BasicNameValuePair("default_persistent","0"));
			parameters.add(new BasicNameValuePair("charset_test",""));
			parameters.add(new BasicNameValuePair("timezone","300"));
			parameters.add(new BasicNameValuePair("lgnrnd",""));
			parameters.add(new BasicNameValuePair("lgnjs",""));
			parameters.add(new BasicNameValuePair("locale","ko_KR"));
			login.setEntity(new UrlEncodedFormEntity(parameters));
			
			response = this.client.execute(login);
			entity = response.getEntity();
			if(entity != null) EntityUtils.consume(entity);
			List<Cookie> cookies = this.client.getCookieStore().getCookies();
	        for (int i = 0; i < cookies.size(); i++) {
	        	if (cookies.get(i).getName().equals("c_user") && !cookies.get(i).getValue().equals("")) {
	        		sessionSet = true;
	        	}
	        }
			// Success
			if (sessionSet) {
				System.out.println("Successfully logged in.");
				return true;
			} else {
				System.out.println("Login failed.");
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		;
		return true;
	}
}
