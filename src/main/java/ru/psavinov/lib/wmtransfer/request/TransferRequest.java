package ru.psavinov.lib.wmtransfer.request;

import java.io.InputStream;
import java.security.Signature;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.psavinov.lib.wmtransfer.exception.IncorrectDestinationException;
import ru.psavinov.lib.wmtransfer.response.TransferResponse;
import ru.psavinov.lib.wmtransfer.response.WMResponse;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class TransferRequest extends AbstractRequest {

	public TransferRequest(Signature signer, String wmId, long transactionId,
			String purse,String currency,String phoneNumber,double amount,
			Date date, long pointId, boolean test) {
		setSigner(signer);
		setProperty(WMID,wmId);
		if (purse != null && !purse.equals("")){
		    setProperty(PURSE,purse);
		}
		setProperty(CURRENCY,currency);
		setProperty(ID,transactionId + "");
		setProperty(TEST,(test ? 1 : 0) + "");
		setProperty(POINT,pointId + "");
		setProperty(DATE,dateFormat.format(date));
		if (phoneNumber != null && !phoneNumber.equals("")) {
			setProperty(PHONE,phoneNumber.replaceAll("[^\\d]", ""));
		}
		setProperty(PRICE,formatAmount(amount));
	}

	public HttpPost prepareRequest() throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement(W3SREQUEST);
		Element id = doc.createElement(WMID);
		Element sign = doc.createElement(SIGN);
		sign.setAttribute(TYPE, String.valueOf(2));
		sign.setTextContent(createSignature(WMID,ID,CURRENCY,TEST,PURSE,PHONE,PRICE,DATE,POINT));
		id.setTextContent(getWMID());
		root.appendChild(id);
		root.appendChild(sign);
		Element pe = doc.createElement(PAYMENT);
		pe.setAttribute(CURRENCY, getProperty(CURRENCY));
		pe.setAttribute(ID, getProperty(ID));
		pe.setAttribute(TEST, getProperty(TEST));
		boolean destination = false;
		if (getProperty(PURSE) != null && !getProperty(PURSE).equals("")) {
			Element pue = doc.createElement(PURSE);
			pue.setTextContent(getProperty(PURSE));
			pe.appendChild(pue);
			destination = true;
		}
		
		if (getProperty(PHONE) != null && !getProperty(PHONE).equals("")) {
			Element phe = doc.createElement(PHONE);
			phe.setTextContent(getProperty(PHONE));
			pe.appendChild(phe);
			destination = true;
		}
		
		if (!destination) {
			throw new IncorrectDestinationException("No payment recepient specified, phone number or purse required!");
		}
		
		Element de = doc.createElement(DATE);
		de.setTextContent(getProperty(DATE));
		pe.appendChild(de);
		
		Element pte = doc.createElement(POINT);
		pte.setTextContent(getProperty(POINT));
		pe.appendChild(pte);
		
		Element pre = doc.createElement(PRICE);
		pre.setTextContent(getProperty(PRICE));
		pe.appendChild(pre);
		root.appendChild(pe);
		doc.appendChild(root);
		HttpPost post = new HttpPost(PAYMENT_API_URL);
		post.setEntity(new StringEntity(XMLUtil.XMLToString(doc),"utf-8"));
		return post;
	}

	public WMResponse parseResponse(InputStream stream) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(stream);
		TransferResponse r = new TransferResponse();
		r.parseReturnBase(doc);
		
		if (r.getRetVal() == 0) {
			r.parseResponse(doc);
		}
		
		return r;
	}
	
}
