import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SuperSimpleHttpsSender{

  public static byte[] sendWithOutFramework(String targetURL, byte[] request){
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(trustAllHosts);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection connection = null;
		try {
			//Create connection
			URL url = new URL(targetURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length",
			                              Integer.toString(request.length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			//Send request
			connection.getOutputStream().write(request, 0, request.length);
			//Get Response
			InputStream is = connection.getInputStream();
			int read = 0;
			byte[] buf = new byte[8196];
			try(ByteArrayOutputStream boas =  new ByteArrayOutputStream() ) {
				while ((read = is.read(buf)) > 0) {
					boas.write(buf, 0, read);
				}
				return boas.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	private static HostnameVerifier trustAllHosts =  new HostnameVerifier(){

		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}

	};
	private static TrustManager[] trustAllCerts = new TrustManager[] {
	                                                   new X509TrustManager() {
	                                                       @Override
														public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                                                           return new X509Certificate[0];
	                                                       }
	                                                       @Override
														public void checkClientTrusted(
	                                                           java.security.cert.X509Certificate[] certs, String authType) {
	                                                    	   //don't care
	                                                           }
	                                                       @Override
														public void checkServerTrusted(
	                                                           java.security.cert.X509Certificate[] certs, String authType) {
	                                                    	   //don't care
	                                                       }
	                                                   }
	                                               };



}
