package ru.psavinov.lib.wmtransfer.request;

import java.io.InputStream;
import java.security.Signature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.psavinov.lib.wmtransfer.response.CheckResponse;
import ru.psavinov.lib.wmtransfer.response.WMResponse;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;

public class CheckRequest extends AbstractRequest {

	public CheckRequest(Signature signer, String wmId,String purse,String currency,String phoneNumber,double amount) {
		setSigner(signer);
		setProperty(WMID,wmId);
		if (purse != null && !purse.equals("")) {
		    setProperty(PURSE,purse);
		}
		setProperty(CURRENCY,currency);
		if (phoneNumber != null && !phoneNumber.equals("")) {
			setProperty(PHONE,phoneNumber);
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
		sign.setTextContent(createSignature(WMID,CURRENCY,PURSE,PHONE,PRICE));
		id.setTextContent(getWMID());
		root.appendChild(id);
		root.setAttribute("lang","en");
		Element lng = doc.createElement("lang");
		lng.setTextContent("en");
		root.appendChild(lng);
		root.appendChild(sign);
		Element pe = doc.createElement(PAYMENT);
		pe.setAttribute(CURRENCY, getProperty(CURRENCY));
		if (getProperty(PURSE) != null && !getProperty(PURSE).equals("")) {
		    Element pue = doc.createElement(PURSE);
		    pue.setTextContent(getProperty(PURSE));
		    pe.appendChild(pue);
		}
		if (getProperty(PHONE) != null && !getProperty(PHONE).equals("")) {
			Element phe = doc.createElement(PHONE);
			phe.setTextContent(getProperty(PHONE));
			pe.appendChild(phe);
		}
		Element pre = doc.createElement(PRICE);
		pre.setTextContent(getProperty(PRICE));
		pe.appendChild(pre);
		root.appendChild(pe);
		doc.appendChild(root);
		HttpPost post = new HttpPost(PREPAYMENT_API_URL);
		post.setEntity(new StringEntity(XMLUtil.XMLToString(doc),"utf-8"));
		return post;
	}

	public WMResponse parseResponse(InputStream stream) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(stream);
		CheckResponse r = new CheckResponse();
		r.parseReturnBase(doc);
		
		if (r.getRetVal() == 0) {
			r.parseResponse(doc);
		}
		
		return r;
	}
	
}
