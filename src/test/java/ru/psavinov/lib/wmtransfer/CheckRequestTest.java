package ru.psavinov.lib.wmtransfer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import ru.psavinov.lib.wmtransfer.response.CheckResponse;


public class CheckRequestTest extends WMTest {
	
	@Ignore
	@Test
	public void testCheckRequest() throws Exception { 
		CheckResponse response = wmt.checkTransfer("PurseNumberHere", "USD", null, 1.50);
		// empty purse
		assertTrue(!response.isSuccess());
		// check for description text
		assertNotNull(response.getDescription());
		assertTrue(!response.getDescription().equals(""));
	}

}
