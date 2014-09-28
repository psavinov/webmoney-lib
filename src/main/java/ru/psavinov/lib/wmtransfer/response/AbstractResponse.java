package ru.psavinov.lib.wmtransfer.response;

import org.w3c.dom.Document;

import ru.psavinov.lib.wmtransfer.util.XMLUtil;


public abstract class AbstractResponse implements WMResponse {
	
	public abstract void parseResponse(Document d);
	
	private long retval;
	private String retdesc;
	private String description;
	private boolean success;
	
	public String getRetDesc() {
		return retdesc;
	}
	
	public void setRetDesc(String retdesc) {
		this.retdesc = retdesc;
	}
	
	public long getRetVal() {
		return retval;
	}
	
	public void setRetVal(long retval) {
		this.retval = retval;
	}
	
	public void parseReturnBase(Document d) {
		String retValString = XMLUtil.getTagValue(d, RET_VAL);
		String retDscString = "";
		String descriptionString = "";
		try {
		    retDscString = new String(XMLUtil.getTagValue(d, RET_DESC).getBytes("cp1251"));
		    descriptionString = new String(XMLUtil.getTagValue(d, DESCRIPTION).getBytes("cp1251"));
		} catch (Exception e){
		    e.printStackTrace();
		}
		try {
			setRetVal(Long.parseLong(retValString));
		} catch (Exception ex) {
			setRetVal(0);
		}
		
		setSuccess(getRetVal() == 0);
		
		setRetDesc(retDscString);
		setDescription(descriptionString);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
