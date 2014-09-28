package ru.psavinov.lib.wmtransfer.request;

import java.io.UnsupportedEncodingException;
import java.security.Signature;
import java.security.SignatureException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

public abstract class AbstractRequest implements WMRequest {
	
	private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
	
	private Signature signer;
	
	public String createSignature(String... props) throws UnsupportedEncodingException, SignatureException {
		StringBuffer buffer = new StringBuffer();
		for (String pName : props) {
			if (getProperty(pName)!=null)
				buffer.append(getProperty(pName));
		}
		byte[] dataToSign = buffer.toString().getBytes("utf-8");
		getSigner().update(dataToSign);
		String out = new String(Base64.encodeBase64(getSigner().sign()),"utf-8");
		return out;
	}
	
	static {
		numberFormat.setMaximumFractionDigits(1);
		numberFormat.setMinimumFractionDigits(1);
	}
	
	public static String formatAmount(double amount) {
		int dPart = (int) amount;
		double fPart = amount - dPart;
		if (fPart > 0.0){
		    return numberFormat.format(amount);
		} else {
		    return (dPart+"");
		}
	}

	private Properties properties;
	
	public AbstractRequest() {
		this.properties = new Properties();
	}

	public Properties getProperties() {
		return properties;
	}
	
	public void setProperty(String name, String value) {
		getProperties().put(name, value);
	}
	
	public String getProperty(String name) {
		return getProperties().getProperty(name);
	}
	
	public String getWMID() {
		return getProperties().getProperty(WMID) != null ?
				getProperties().getProperty(WMID) : "";
	}

	public Signature getSigner() {
		return signer;
	}

	public void setSigner(Signature signer) {
		this.signer = signer;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	private String language;

}
