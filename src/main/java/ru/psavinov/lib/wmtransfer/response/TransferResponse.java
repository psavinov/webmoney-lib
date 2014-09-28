package ru.psavinov.lib.wmtransfer.response;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.psavinov.lib.wmtransfer.request.WMRequest;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class TransferResponse extends AbstractResponse {
	
	public void parseResponse(Document d) {
		NodeList nodeList = d.getElementsByTagName(WMRequest.PAYMENT);
		if (nodeList != null && nodeList.getLength() == 1) {
			Node pn = nodeList.item(0);
			Node cn = pn.getAttributes().getNamedItem(WMRequest.CURRENCY);
			if (cn != null && cn.getNodeValue()!= null && !cn.getNodeValue().equals("")) {
				setCurrency(cn.getNodeValue().trim());
			}
			
			Node sn = pn.getAttributes().getNamedItem(WMRequest.ID);
			if (sn != null && sn.getNodeValue()!= null && !sn.getNodeValue().equals("")) {
				setId(Long.parseLong(sn.getNodeValue()));
			}
			
			Node mn = pn.getAttributes().getNamedItem(WMRequest.TEST);
			if (mn != null && mn.getNodeValue()!= null && !mn.getNodeValue().equals("")) {
				setTest(mn.getNodeValue().equals("0") ? false : true);			
			}
		}
		
		setPurse(XMLUtil.getTagValue(d, WMRequest.PURSE));
		setPhone(XMLUtil.getTagValue(d, WMRequest.PHONE));
		setPointId(Long.parseLong(XMLUtil.getTagValue(d, WMRequest.POINT)));
		setExternalId(XMLUtil.getTagValue(d, WMRequest.WMTRAN_ID));
		
		setPrice(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.PRICE)));
		setAmount(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.AMOUNT)));
		setCommission(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.COMMISSION)));
		setRest(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.REST)));
		
		try {
			setInsertDate(WMRequest.dateFormat.parse(XMLUtil.getTagValue(d, WMRequest.DATE)));
			setProcessDate(WMRequest.dateFormat.parse(XMLUtil.getTagValue(d, WMRequest.DATE_UPD)));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	private String currency;
	private String purse;
	private String phone;
	private String externalId;
	private long id;
	private long pointId;
	private boolean test;
	private BigDecimal price;
	private BigDecimal amount;
	private BigDecimal commission;
	private BigDecimal rest;
	private Date processDate;
	private Date insertDate;	

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPurse() {
		return purse;
	}

	public void setPurse(String purse) {
		this.purse = purse;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPointId() {
		return pointId;
	}

	public void setPointId(long pointId) {
		this.pointId = pointId;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getRest() {
		return rest;
	}

	public void setRest(BigDecimal rest) {
		this.rest = rest;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

}
