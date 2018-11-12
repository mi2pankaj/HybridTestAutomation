package tpt.googleAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * 
 * @author Rishi Kataria
 * to switch google sheet account -- login with registered user, create project, create application and download the secret.json file 
 * and allow access to google api from https://console.developers.google.com 
 * finally delete files created under directory ==> System.getProperty("user.home")/.credentials
 *
 */

public class GoogleSheetAPI {

	Logger logger = Logger.getLogger(GoogleSheetAPI.class.getName());

	/** Application name. */
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/sheets.googleapis.com-java-quickstart
	 */
	
	/** commenting the scope and using the below one to read and write */
	//	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/** Load client secret
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize(){

		Credential credential = null;
		try
		{
			//			String a = System.getProperty("user.dir")+"/client_secret.json";
			//			System.out.println("user.dir : " +a);
			InputStream in=GoogleSheetAPI.class.getResourceAsStream("/client_secret.json");
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			/** build Flow and trigger user authorization request */
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
					.setDataStoreFactory(DATA_STORE_FACTORY)
					.setAccessType("offline")
					.build();

			credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

			System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return credential;
	}
	
	/**
	 * Build and return an authorized Sheets API client service.
	 * @return an authorized Sheets API client service
	 * @throws IOException
	 */
	public Sheets getSheetsService() throws IOException {

		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
	

	public List<List<Object>> getSpreadSheetRecords(String spreadsheetId, String range) {

		List<List<Object>> values = new ArrayList<>();
		try
		{
			Sheets service = null;
			ValueRange response = null;

			/** adding a retry code - with a sleep period of 5 sec.  */
			try {
				service = getSheetsService();		
				response = service.spreadsheets().values()
						.get(spreadsheetId, range)
						.execute();
			}catch (Exception e) {

				e.printStackTrace();
				
				logger.info("retrying to get values from google sheet coz of : "+e.getMessage());

				Thread.sleep(5000);
				service = getSheetsService();		
				response = service.spreadsheets().values()
						.get(spreadsheetId, range)
						.execute();
			}

			values = response.getValues();

			if (values != null && values.size() != 0) {
				return values;
			} else {
				System.out.println("No data found.");
				return null;
			}
		}catch (Exception e) {
			logger.error("##### ERROR OCCURRED WHILE READING DATA FROM GOOGLE SHEET ####### " + e.getMessage(), e);
			return values;
		}

	}

}
