package ru.psavinov.lib.wmtransfer.request;

import java.io.InputStream;
import java.security.Signature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.psavinov.lib.wmtransfer.response.RefundResponse;
import ru.psavinov.lib.wmtransfer.response.WMResponse;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class RefundRequest extends AbstractRequest {

	public RefundRequest(Signature signer, String wmId, String externalId, String remark) {
		setSigner(signer);
		setProperty(WMID,wmId);
		setProperty(WMTRAN_ID,externalId);
		if (remark == null || remark.equals("") || remark.length()<30) {
			throw new RuntimeException("Remark should contain at least 30 characters!");
		}
		setProperty(REMARK,remark);
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
		sign.setTextContent(createSignature(WMID,REMARK,WMTRAN_ID));
		id.setTextContent(getWMID());
		root.appendChild(id);
		root.appendChild(sign);
		Element pe = doc.createElement(PAYMENT);
		Element wmtranId = doc.createElement(WMTRAN_ID);
		wmtranId.setTextContent(getProperty(WMTRAN_ID));
		pe.appendChild(wmtranId);
		Element remark = doc.createElement(REMARK);
		remark.setTextContent(getProperty(REMARK));
		pe.appendChild(remark);
		root.appendChild(pe);
		doc.appendChild(root);
		HttpPost post = new HttpPost(REFUND_API_URL);
		post.setEntity(new StringEntity(XMLUtil.XMLToString(doc),"utf-8"));
		return post;
	}

	public WMResponse parseResponse(InputStream stream) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(stream);
		RefundResponse r = new RefundResponse();
		r.parseReturnBase(doc);
		
		if (r.getRetVal() == 0) {
			r.parseResponse(doc);
		}
		
		return r;
	}
	
}
