package com.huawei.sharedrive.uam.enterprise.domain;

public enum AccountConfigAttribute {

	/** 云盘租户是否启动组织结构这个模块 */
	DEPARTMENT_ENABLE("customer.storbox.department.enable"),
	/** 否启用企业员工工号作为登录用户名 */
	STAFF_STAFFID_ENABLE("customer.storbox.staff.staffid.enable"),
	/** 员工信息安全等级控制 */
	STAFF_SECRETLEVEL_ENABLE("customer.storbox.staff.secretlevel.enable"),
	/** 是否启用病毒扫描 **/
	DOC_VIRUSSCAN_ENABLE("customer.storbox.doc.virusscan.enable"),
	/** 是否启用短信网关 **/
	SMS_ENABLE("customer.storbox.sms.enable"),
	/** 是否启用发件服务器 **/
	MAIL_STMP_ENABLE("customer.storbox.mail.stmp.enable"),
	/** 发件服务器的地址 */
	MAIL_STMP_SERVER("customer.storbox.mail.stmp.server"),
	/** 发件服务器的端口 */
	MAIL_STMP_PORT("customer.storbox.system.mail.stmp.port"),
	/** 发件服务器是否进行发件认证 */
	MAIL_STMP_AUTH_ENABLE("customer.storbox.mail.stmp.auth.enable"),
	/** 发件服务器认证方式 */
	MAIL_STMP_SECURITY("customer.storbox.mail.stmp.security"),
	/** 发件账号 */
	MAIL_STMP_USERNAME("customer.storbox.mail.stmp.username"),
	/** 发件账号加密后的密码 */
	MAIL_STMP_PASSWORD("customer.storbox.mail.stmp.password"),
	/** 发件账号密码的加密key */
	MAIL_STMP_PASSWORD_KEY("customer.storbox.mail.stmp.password.key"),
	/** 是否启用二级域名 */
	URL_SUBDOMAIN_NAME("customer.storbox.subdomain.enable"),
	/** 是否启用独立域名 */
	URL_SUBDOMAIN_VALUE("customer.storbox.subdomain.value"),
	/** 是否启用独立域名 */
	URL_INDDOMAIN_NAME("customer.storbox.inddomain.enable"),
	/** 是否启用独立域名 */
	URL_INDDOMAIN_VALUE("customer.storbox.inddomain.value"),
	/** 启用登陆失败通知 */
	SECURITY_LOGINFAIL_NOTICE_ENABLE("customer.storbox.security.loginfail.notice.enable"),
	/** 关键信息手机验证 */
	MOBILE_VALIDATE_ENABLE("customer.storbox.mobile.validate.enable"),
	/** 登陆失败重试允许的最大尝试次数 */
	SECURITY_LOGINFAIL_TRY_TIMES("customer.storbox.security.loginfail.try.times"),
	/** 超过最大尝试次数账号锁定时间 */
	SECURITY_LOGINFAIL_LOCK_TIME("customer.storbox.security.loginfail.lock.time"),
	/** 密码复杂度 */
	security_password_rule("customer.storbox.security.password.rule"),
	/** 是否启用文档密级控制功能 **/
	DOC_SECRETLEVEL_ENABLE("customer.storbox.doc.secretlevel.enable"),
	/** 是否支持外协人员 */
	user_external_enable("customer.storbox.user.external.enable"),

	/** 文件外发是否需要审批 */
	SENDFILE_APPROVE_ENABLE("customer.storbox.sendfile.approve.enable"),

	/** 未启用文档密级，外协账号 */
	NORMAL_EXTUSER_FORBID("customer.storbox.sendfile.normal.extuser.forbid"),
	/** 启用文档密级，外协账号 */
	SECRET_EXTUSER_FORBID("customer.storbox.sendfile.secretlevel.extuser.forbid"),

	/** 文件外发是否需要审批 */
	NORMAL_APPROVE_ENABLE("customer.storbox.sendfile.normal.approve.enable"),
	NOTSECRET_APPROVE_ENABLE("customer.storbox.sendfile.notsecret.approve.enable"), 
	SECRETDOC_APPROVE_ENABLE("customer.storbox.sendfile.secretdoc.approve.enable"),
	/** 文件外发审批人 */
	NORMAL_APPROVE_ID("customer.storbox.sendfile.normal.approve.id"), 
	NOTSECRET_APPROVE_ID("customer.storbox.sendfile.notsecret.approve.id"), 
	SECRETDOC_APPROVE_ID("customer.storbox.sendfile.secretdoc.approve.id"),
	/** 文件外发范围 */
	NORMAL_APPROVE_RANGE("customer.storbox.sendfile.normal.approve.range"), 
	NOTSECRET_APPROVE_RANGE("customer.storbox.sendfile.notsecret.approve.range"), 
	SECRETDOC_APPROVE_RANGE("customer.storbox.sendfile.secretdoc.approve.range"),
	/** 密级文档外发等级 ， Secret/moreSecret/topSecret */
	SENDFILE_SECRETDOC_LEVEL("customer.storbox.sendfile.secretdoc.level"),
	/** 启动安全网络访问控制机制 */
	VISIT_SECURITYNET_ENABLE("customer.storbox.visit.securitynet.enable"),
	/** 启动密级文档持有限制 */
	VISIT_SECRETDOC_ENABLE("secretdoc.visitLimit.enable"),
	/** 允许安全信息管理员设置密级文档 */
	SECRETDOC_SECURITY_CREATOR_ENABLE("secretdoc.securityCreator.enable"),
	/** 允许员工设置密级文档 */
	SECRETDOC_STAFF_CREATOR_ENABLE("secretdoc.staffCreator.enable"),
	/** 员工要求安全等级 */
	SECRETDOC_STAFF_CREATOR_SECRETLEVEL("secretdoc.staffCreator.secretlevel"),
	ENTERPRISE_ACCOUNT_SPACE_QUOTA("customer.enterprise.maxUserQuota");

	private String name;

	private AccountConfigAttribute(String name) {
		this.name = name;
	}

	public static AccountConfigAttribute getAccountConfigAttribute(String name) {
		for (AccountConfigAttribute attribute : AccountConfigAttribute.values()) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

}
