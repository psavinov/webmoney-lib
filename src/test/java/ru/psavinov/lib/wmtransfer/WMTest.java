package ru.psavinov.lib.wmtransfer;

import java.io.FileInputStream;

import org.junit.Before;

import ru.psavinov.lib.wmtransfer.WMTransfer;

public abstract class WMTest {
	
	public static final String WM_KEYSTORE_PASSWORD = "keystorePasswordHere";
	public static final String WM_KEY_PASSWORD = "privateKeyPasswordHere";
	public static final String WMID_KEY_FILE = "private_key_file.p12";
	public static final String WM_KEYSTORE_FILE = "root_keystore_file.keystore";
	public static final String WMID = "WebMoneyIDforTestHere";
	
	public WMTransfer wmt;
	
	@Before
	public void initWMTransfer() throws Exception {
		wmt = new WMTransfer();
		wmt.initialize(WMID,new FileInputStream(WM_KEYSTORE_FILE),WM_KEYSTORE_PASSWORD,
				new FileInputStream(WMID_KEY_FILE),WM_KEY_PASSWORD);
	}
	
}
