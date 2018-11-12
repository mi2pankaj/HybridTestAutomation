package poc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

public class Base64Encode {
	private final String USER_AGENT = "Mozilla/5.0";
	
	public void encodeEmail(String email) throws UnsupportedEncodingException{
		String encodedEmail=Base64.getEncoder().encodeToString(email.getBytes("UTF-8"));
		System.out.println(encodedEmail);
	}
	public void postRequest() throws IOException{
		String url = "http://www.lenskart.com/me/index/removeAccount";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		Base64Encode base=new Base64Encode();
		base.encodeEmail("aapkiparty@gmail.com");
	}

}
