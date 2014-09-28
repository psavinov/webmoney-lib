package ru.psavinov.lib.wmtransfer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import ru.psavinov.lib.wmtransfer.exception.InitializationException;
import ru.psavinov.lib.wmtransfer.exception.WMRequestException;
import ru.psavinov.lib.wmtransfer.request.CheckRequest;
import ru.psavinov.lib.wmtransfer.request.HistoryRequest;
import ru.psavinov.lib.wmtransfer.request.RefundRequest;
import ru.psavinov.lib.wmtransfer.request.TransferRequest;
import ru.psavinov.lib.wmtransfer.response.CheckResponse;
import ru.psavinov.lib.wmtransfer.response.HistoryResponse;
import ru.psavinov.lib.wmtransfer.response.RefundResponse;
import ru.psavinov.lib.wmtransfer.response.TransferResponse;


public class WMTransfer {

	/**
	 * Initialization of WebMoney Transfer object to perform all operations
	 * 
	 * @param id WebMoney ID number
	 * @param rootStoreIS Input stream of root certificate storage
	 * @param rootStorePassword Root certificate storage password
	 * @param keyStoreIS Input stream of private key storage
	 * @param keyStorePassword Private key storage password
	 * @param lang
	 */
	public void initialize(String id, InputStream rootStoreIS,
			String rootStorePassword, InputStream keyStoreIS,
			String keyStorePassword, String lang) {
		try {
			/* define session language, english by default */
			setLanguage(lang != null ? lang : "en");
			
			/* open and load root certificate and private key for signing */
			KeyStore rootStore = KeyStore
					.getInstance("JKS","SUN");
			rootStore.load(rootStoreIS, 
					rootStorePassword.toCharArray());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(keyStoreIS, 
					keyStorePassword.toCharArray());
			Enumeration<?> enumeration = keyStore.aliases();
			PrivateKey key = (PrivateKey) keyStore.
					getKey(enumeration.nextElement().
					toString(), keyStorePassword.toCharArray());
			signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(key);
			
			/* initialize https socket factory to skip certificate 
			 * validation, can be added later
			 */
			HttpsURLConnection
					.setDefaultHostnameVerifier(SSLSocketFactory.
							ALLOW_ALL_HOSTNAME_VERIFIER);
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			Scheme sch = new Scheme("https", 443, ssf);
			getHttpClient().getConnectionManager().getSchemeRegistry()
					.register(sch);
			setInitialized(true);
			setWMID(id);
			
			/* ready, object initialized, can be used to transfer funds */
			
		} catch (Throwable ex) { 
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			try {
				rootStoreIS.close();
				keyStoreIS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void initialize(String id, String rootStorePath,
			String rootStorePassword, String keyStorePath,
			String keyStorePassword) {
		try {
			initialize(id, new FileInputStream(rootStorePath), rootStorePassword,
					new FileInputStream(keyStorePath), keyStorePassword, "en");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void initialize(String id, FileInputStream rootStoreIS,
			String rootStorePassword, FileInputStream keyStoreIS,
			String keyStorePassword) {
		initialize(id, rootStoreIS, rootStorePassword, keyStoreIS,
				keyStorePassword, "en");
	}

	/**
	 * Check a possibilty for a payment
	 * 
	 * @param purse
	 *            WMT purse number
	 * @param currency
	 *            Currency alpha-3 code
	 * @param phoneNumber
	 *            Buyer phone number
	 * @param amount
	 *            Payment amount
	 * @return Check response object
	 * @throws Exception
	 */
	public CheckResponse checkTransfer(String purse, String currency,
			String phoneNumber, double amount) throws Exception {
		if (isInitialized()) {
			CheckRequest req = new CheckRequest(getSigner(), getWMID(), purse,
					currency, phoneNumber, amount);
			req.setLanguage(getLanguage());
			HttpResponse resp = getHttpClient().execute(req.prepareRequest());
			HttpEntity ent = resp.getEntity();
			CheckResponse response = (CheckResponse) req.parseResponse(ent
					.getContent());
			if (response.getRetVal() != 0) {
				throw new WMRequestException(response.getRetVal(),
						response.getDescription() != null ? response
								.getDescription() : response.getRetDesc());
			}
			return response;
		}

		throw new InitializationException("WebMoney transfer not initialized!");
	}

	/**
	 * Transfer funds to some purse or buy a WMT check
	 * 
	 * @param transactionId
	 *            Internal transaction identifier
	 * @param purse
	 *            WMT Purse number
	 * @param currency
	 *            Currency alpha-3 code
	 * @param phoneNumber
	 *            Phone number (in case of WMT check)
	 * @param amount
	 *            Transfer amount
	 * @param date
	 *            Transaction insert date
	 * @param pointId
	 *            Internal point identifier
	 * @param test
	 *            True if test payment, false otherwise
	 * @return Reponse object with all parameters of payment
	 * @throws Exception
	 */
	public TransferResponse transferMoney(long transactionId, String purse,
			String currency, String phoneNumber, double amount, Date date,
			long pointId, boolean test) throws Exception {
		if (isInitialized()) {
			TransferRequest req = new TransferRequest(getSigner(), getWMID(),
					transactionId, purse, currency, phoneNumber, amount, date,
					pointId, test);
			req.setLanguage(getLanguage());
			HttpResponse resp = getHttpClient().execute(req.prepareRequest());
			HttpEntity ent = resp.getEntity();
			TransferResponse response = (TransferResponse) req
					.parseResponse(ent.getContent());
			if (response.getRetVal() != 0) {
				throw new WMRequestException(response.getRetVal(),
						response.getDescription() != null ? response
								.getDescription() : response.getRetDesc());
			}
			return response;

		}

		throw new InitializationException("WebMoney transfer not initialized!");
	}

	/**
	 * Request payments history from server
	 * 
	 * @param dateStart
	 *            Date interval start (required)
	 * @param dateEnd
	 *            Date interval end (required)
	 * @param externalId
	 *            WMT payment identifier
	 * @return Response object with payments history
	 * @throws Exception
	 */
	public HistoryResponse requestHistory(Date dateStart, Date dateEnd,
			String externalId) throws Exception {
		if (isInitialized()) {
			HistoryRequest req = new HistoryRequest(getSigner(), getWMID(),
					dateStart, dateEnd, externalId);
			req.setLanguage(getLanguage());
			HttpResponse resp = getHttpClient().execute(req.prepareRequest());
			HttpEntity ent = resp.getEntity();
			HistoryResponse response = (HistoryResponse) req.parseResponse(ent
					.getContent());
			if (response.getRetVal() != 0) {
				throw new WMRequestException(response.getRetVal(),
						response.getDescription() != null ? response
								.getDescription() : response.getRetDesc());
			}
			return response;

		}

		throw new InitializationException("WebMoney transfer not initialized!");
	}

	/**
	 * Refund processed transaction by externam Webmoney Transfer identified and
	 * remark text.
	 * 
	 * @param externalId
	 *            External WMT identifier
	 * @param remark
	 *            Refund remark text (more than 30 characters)
	 * @return Refund response object
	 * @throws Exception
	 */
	public RefundResponse refundPayment(String externalId, String remark)
			throws Exception {
		if (isInitialized()) {
			RefundRequest req = new RefundRequest(getSigner(), getWMID(),
					externalId, remark);
			req.setLanguage(getLanguage());
			HttpResponse resp = getHttpClient().execute(req.prepareRequest());
			HttpEntity ent = resp.getEntity();
			RefundResponse response = (RefundResponse) req.parseResponse(ent
					.getContent());
			if (response.getRetVal() != 0) {
				throw new WMRequestException(response.getRetVal(),
						response.getDescription() != null ? response
								.getDescription() : response.getRetDesc());
			}
			return response;

		}

		throw new InitializationException("WebMoney transfer not initialized!");
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	private HttpClient httpClient = new DefaultHttpClient();
	private boolean initialized = false;
	private String WMID;
	private Signature signer;
	private String language;

	private Signature getSigner() {
		return this.signer;
	}
	
	private void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	private String getWMID() {
		return WMID;
	}

	private void setWMID(String wMID) {
		WMID = wMID;
	}
}
