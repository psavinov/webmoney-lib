package ru.psavinov.lib.wmtransfer.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.psavinov.lib.wmtransfer.PaymentDirection;
import ru.psavinov.lib.wmtransfer.request.WMRequest;
import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public class HistoryResponse extends AbstractResponse {

	public void parseResponse(Document d) {
		NodeList l = d.getElementsByTagName(WMRequest.HISTORY);
		if (l != null && l.getLength() == 1) {
			Node hn = l.item(0).getAttributes().getNamedItem(WMRequest.CNT);
			if (hn.getNodeValue() != null && !hn.getNodeValue().equals("")){
				setCount(Integer.parseInt(hn.getNodeValue()));
			}
		}
		String dateStartString = XMLUtil.getTagValue(d, WMRequest.DATE_START);
		String dateEndString = XMLUtil.getTagValue(d, WMRequest.DATE_END);
		try {
			setDateStart(WMRequest.dateFormat.parse(dateStartString));
			setDateEnd(WMRequest.dateFormat.parse(dateEndString));
		} catch (ParseException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		NodeList pnList = d.getElementsByTagName(WMRequest.PAYMENT);
		if (pnList != null && pnList.getLength() > 0) {
			for (int k = 0; k < pnList.getLength(); k++) {
				Node pn = pnList.item(k);
				WMPayment payment = new WMPayment();
				payment.setId(Long.parseLong(XMLUtil.getAttribute(pn, WMRequest.ID)));
				payment.setCurrency(!XMLUtil.getAttribute(pn, WMRequest.CURRENCY).equals("") ? 
						XMLUtil.getAttribute(pn, WMRequest.CURRENCY) : "EUR");
				payment.setForMerchant(XMLUtil.getAttribute(pn, WMRequest.MERCHANT).trim().equals("1"));
				payment.setTest(XMLUtil.getAttribute(pn, WMRequest.TEST).trim().equals("1"));
				if(XMLUtil.getAttribute(pn, WMRequest.STATUS).trim().equals("purse")){
					payment.setDirection(PaymentDirection.purse);
				} else {
					payment.setDirection(PaymentDirection.check);
				}
				for (int q=0;q<pn.getChildNodes().getLength();q++) {
					Node pcn = pn.getChildNodes().item(q);
					if (pcn.getNodeName().trim().equals(WMRequest.PURSE)) {
						payment.setPurse(pcn.getTextContent().trim());
					}
					if (pcn.getNodeName().trim().equals(WMRequest.PHONE)) {
						payment.setPhone(pcn.getTextContent().trim());
					}
					if (pcn.getNodeName().trim().equals(WMRequest.WMTRAN_ID)) {
						payment.setExternalId(pcn.getTextContent().trim());
					}
					if (pcn.getNodeName().trim().equals(WMRequest.POINT)) {
						payment.setPointId(Long.parseLong(pcn.getTextContent().trim()));
					}
					if (pcn.getNodeName().trim().equals(WMRequest.PRICE)) {
						payment.setPrice(new BigDecimal(pcn.getTextContent().trim()).setScale(2, RoundingMode.HALF_UP));
					}
					if (pcn.getNodeName().trim().equals(WMRequest.REST)) {
						payment.setRest(new BigDecimal(pcn.getTextContent().trim()).setScale(2, RoundingMode.HALF_UP));
					}
					if (pcn.getNodeName().trim().equals(WMRequest.COMMISSION)) {
						payment.setCommission(new BigDecimal(pcn.getTextContent().trim()).setScale(2, RoundingMode.HALF_UP));
					}
					if (pcn.getNodeName().trim().equals(WMRequest.AMOUNT)) {
						payment.setAmount(new BigDecimal(pcn.getTextContent().trim()).setScale(2, RoundingMode.HALF_UP));
					}
					if (pcn.getNodeName().trim().equals(WMRequest.DATE)) {
						try {
							payment.setInsertDate(WMRequest.dateFormat.parse(pcn.getTextContent().trim()));
						} catch (ParseException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}	
					if (pcn.getNodeName().trim().equals(WMRequest.DATE_UPD)) {
						try {
							payment.setProcessDate(WMRequest.dateFormat.parse(pcn.getTextContent().trim()));
						} catch (ParseException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
				
				getPayments().add(payment);
			}
		}
		
	}

	class WMPayment {

		private long id;
		private boolean forMerchant;
		private PaymentDirection direction;
		private boolean test;
		private Date insertDate;
		private Date processDate;
		private String purse;
		private String currency;
		private String phone;
		private long pointId;
		private String externalId;
		private BigDecimal amount;
		private BigDecimal price;
		private BigDecimal commission;
		private BigDecimal rest;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
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

		public boolean isTest() {
			return test;
		}

		public void setTest(boolean test) {
			this.test = test;
		}

		public Date getInsertDate() {
			return insertDate;
		}

		public void setInsertDate(Date insertDate) {
			this.insertDate = insertDate;
		}

		public Date getProcessDate() {
			return processDate;
		}

		public void setProcessDate(Date processDate) {
			this.processDate = processDate;
		}

		public String getPurse() {
			return purse;
		}

		public void setPurse(String purse) {
			this.purse = purse;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public long getPointId() {
			return pointId;
		}

		public void setPointId(long pointId) {
			this.pointId = pointId;
		}

		public String getExternalId() {
			return externalId;
		}

		public void setExternalId(String externalId) {
			this.externalId = externalId;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
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

	}

	private Date dateStart;
	private Date dateEnd;
	private int count;
	private List<WMPayment> payments;

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<WMPayment> getPayments() {
		if (payments == null) {
			payments = new ArrayList<WMPayment>();
		}
		return payments;
	}

	public void setPayments(List<WMPayment> payments) {
		this.payments = payments;
	}

}
