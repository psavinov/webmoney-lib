package ru.psavinov.lib.wmtransfer.response;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.psavinov.lib.wmtransfer.request.WMRequest;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class RefundResponse extends AbstractResponse {

	public void parseResponse(Document d) {
		NodeList nodeList = d.getElementsByTagName(WMRequest.PAYMENT);
		if (nodeList != null && nodeList.getLength() == 1) {
			Node pn = nodeList.item(0);
			Node cn = pn.getAttributes().getNamedItem(WMRequest.ID);
			if (cn != null && cn.getNodeValue() != null
					&& !cn.getNodeValue().equals("")) {
				setId(Long.parseLong(cn.getNodeValue().trim()));
			}
			setExternalId(XMLUtil.getTagValue(d, WMRequest.WMTRAN_ID));
			setRemark(XMLUtil.getTagValue(d, WMRequest.REMARK));
		}
	}

	private long id;
	private String remark;
	private String externalId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

}
