package ru.psavinov.lib.wmtransfer.response;

import java.math.BigDecimal;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.psavinov.lib.wmtransfer.PaymentDirection;
import ru.psavinov.lib.wmtransfer.request.WMRequest;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class CheckResponse extends AbstractResponse {
	
	public void parseResponse(Document d) {
		NodeList nodeList = d.getElementsByTagName(WMRequest.PAYMENT);
		if (nodeList != null && nodeList.getLength() == 1) {
			Node pn = nodeList.item(0);
			Node cn = pn.getAttributes().getNamedItem(WMRequest.CURRENCY);
			if (cn != null && cn.getNodeValue()!= null && !cn.getNodeValue().equals("")) {
				setCurrency(cn.getNodeValue().trim());
			}
			
			Node sn = pn.getAttributes().getNamedItem(WMRequest.STATUS);
			if (sn != null && sn.getNodeValue()!= null && !sn.getNodeValue().equals("")) {
				if (sn.getNodeValue().trim().equals("purse")) {
					setDirection(PaymentDirection.purse);
				} else {
					setDirection(PaymentDirection.check);
				}
			}
			
			Node mn = pn.getAttributes().getNamedItem(WMRequest.MERCHANT);
			if (mn != null && mn.getNodeValue()!= null && !mn.getNodeValue().equals("")) {
				if (mn.getNodeValue().trim().equals("1")) {
					setForMerchant(true);
				} else {
					setForMerchant(false);
				}				
			}
		}
		
		setPurse(XMLUtil.getTagValue(d, WMRequest.PURSE));
		setPrice(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.PRICE)));
		setAmount(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.AMOUNT)));
		setLimit(new BigDecimal(XMLUtil.getTagValue(d, WMRequest.LIMIT)));
	}

	private String currency;
	private boolean forMerchant;
	private PaymentDirection direction;
	private String purse;
	private BigDecimal price;
	private BigDecimal amount;
	private BigDecimal limit;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isForMerchant() {
		return forMerchant;
	}

	public void setForMerchant(boolean forMerchant) {
		this.forMerchant = forMerchant;
	}

	public PaymentDirection getDirection() {
		return direction;
	}

	public void setDirection(PaymentDirection direction) {
		this.direction = direction;
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

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

}
