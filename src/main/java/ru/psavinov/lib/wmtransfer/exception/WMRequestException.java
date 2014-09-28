package ru.psavinov.lib.wmtransfer.exception;

public class WMRequestException extends Exception {

	private static final long serialVersionUID = 6433519544969597073L;
	private long errorCode;
	private String errorMessage;
	
	public WMRequestException(long code, String msg) {
		setErrorCode(code);
		setErrorMessage(msg);
	}
	
	public String toString() {
		return "WM Transfer error, code = " + errorCode + ", message = " + errorMessage;
	}

	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
