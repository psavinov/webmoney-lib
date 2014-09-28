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

import ru.psavinov.lib.wmtransfer.response.HistoryResponse;
import ru.psavinov.lib.wmtransfer.response.WMResponse;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class HistoryRequest extends AbstractRequest {

	public HistoryRequest(Signature signer, String wmId, Date dateStart, Date dateEnd, String externalId) {
		setSigner(signer);
		setProperty(WMID,wmId);
		setProperty(DATE_START, dateFormat.format(dateStart));
		setProperty(DATE_END, dateFormat.format(dateEnd));
		if (externalId != null) {
			setProperty(WMTRAN_ID, externalId);
		}
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
		sign.setTextContent(createSignature(WMID,DATE_START,DATE_END,WMTRAN_ID));
		id.setTextContent(getWMID());
		root.appendChild(id);
		root.appendChild(sign);
		Element ds = doc.createElement(DATE_START);
		ds.setTextContent(getProperty(DATE_START));
		root.appendChild(ds);
		
		Element de = doc.createElement(DATE_END);
		de.setTextContent(getProperty(DATE_END));
		root.appendChild(de);		
		
		if (getProperty(WMTRAN_ID) != null){
			Element ie = doc.createElement(WMTRAN_ID);
			ie.setTextContent(getProperty(WMTRAN_ID));
			root.appendChild(ie);				
		}
		
		doc.appendChild(root);
		HttpPost post = new HttpPost(HISTORY_API_URL);
		post.setEntity(new StringEntity(XMLUtil.XMLToString(doc),"utf-8"));
		return post;
	}

	public WMResponse parseResponse(InputStream stream) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(stream);
		HistoryResponse r = new HistoryResponse();
		r.parseReturnBase(doc);
		
		if (r.getRetVal() == 0) {
			r.parseResponse(doc);
		}
		
		return r;
	}
	
}
