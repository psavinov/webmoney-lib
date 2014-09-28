package ru.psavinov.lib.wmtransfer.request;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.http.client.methods.HttpPost;

import ru.psavinov.lib.wmtransfer.response.WMResponse;


public interface WMRequest {
	
	public HttpPost prepareRequest() throws Exception;
	public WMResponse parseResponse(InputStream stream) throws Exception;
	
	public static final String PREPAYMENT_API_URL = "https://transfer.gdcert.com/ATM/Xml/PrePayment1.ashx?lang=en";
	public static final String HISTORY_API_URL = "https://transfer.gdcert.com/ATM/Xml/History1.ashx?lang=en";
	public static final String REFUND_API_URL = "https://transfer.gdcert.com/ATM/Xml/RetPayment1.ashx?lang=en";
	public static final String PAYMENT_API_URL = "https://transfer.gdcert.com/ATM/Xml/Payment1.ashx?lang=en";
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	public static final String W3SREQUEST = "w3s.request";
	public static final String WMID = "wmid";
	public static final String SIGN = "sign";
	public static final String TYPE = "type";
	public static final String PAYMENT = "payment";
	public static final String CURRENCY = "currency";
	public static final String PURSE = "purse";
	public static final String PHONE = "phone";
	public static final String PRICE = "price";
	public static final String LIMIT = "limit";
	public static final String AMOUNT = "amount";
	public static final String MERCHANT = "merchant";
	public static final String STATUS = "status";
	public static final String ID = "id";
	public static final String POINT = "point";
	public static final String DATE = "date";
	public static final String DATE_START = "datestart";
	public static final String DATE_END = "dateend";
	public static final String DATE_UPD = "dateupd";
	public static final String WMTRAN_ID = "wmtranid";
	public static final String TEST = "test";
	public static final String REST = "rest";
	public static final String COMMISSION = "comiss";
	public static final String HISTORY = "history";
	public static final String CNT = "cnt";
	public static final String REMARK = "remark";
			
}
