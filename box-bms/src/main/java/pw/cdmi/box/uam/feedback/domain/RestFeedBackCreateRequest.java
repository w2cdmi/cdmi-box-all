package pw.cdmi.box.uam.feedback.domain;

import java.io.Serializable;
import java.util.Date;


import org.springframework.format.annotation.DateTimeFormat;

public class RestFeedBackCreateRequest  implements Serializable

{


	 /**
	 * 
	 */
	private static final long serialVersionUID = 4648161197489099753L;
	
	/**
     * 问题ID
     */
    protected long problemID;

    /**
     * 问题标题
     */
    protected String problemTitle;

    /**
     * 问题类型
     */
    protected String problemType;

    /**
     * 问题描述
     */
    protected String problemDescription;

    /**
     * 提问人ID
     */
    protected String customerID;

    /**
     * 提问人姓名
     */
    protected String customerName;

    /**
     * 提问时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date customerTwTime;

    /**
     * 最新提问时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date newestTwTime;

    /**
     * 最新答复时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date managerAnswerTime;

    /**
     * 问题状态
     */
    protected String problemStatus;

    /**
     * 提问人Email
     */
    protected String customerEmail;
    

	public long getProblemID() {
		return problemID;
	}

	public void setProblemID(long problemID) {
		this.problemID = problemID;
	}

	public String getProblemTitle() {
		return problemTitle;
	}

	public void setProblemTitle(String problemTitle) {
		this.problemTitle = problemTitle;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Date getCustomerTwTime() {
		return customerTwTime;
	}

	public void setCustomerTwTime(Date customerTwTime) {
		this.customerTwTime = customerTwTime;
	}

	public Date getNewestTwTime() {
		return newestTwTime;
	}

	public void setNewestTwTime(Date newestTwTime) {
		this.newestTwTime = newestTwTime;
	}

	public Date getManagerAnswerTime() {
		return managerAnswerTime;
	}

	public void setManagerAnswerTime(Date managerAnswerTime) {
		this.managerAnswerTime = managerAnswerTime;
	}

	public String getProblemStatus() {
		return problemStatus;
	}

	public void setProblemStatus(String problemStatus) {
		this.problemStatus = problemStatus;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

}
