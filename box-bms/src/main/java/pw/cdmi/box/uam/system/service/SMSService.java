package pw.cdmi.box.uam.system.service;

public interface SMSService {
	
	void sendSMS(String templateId,String concatPhone, String password,String price,String identifyCode);
}
