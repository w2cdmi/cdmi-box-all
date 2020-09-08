package com.huawei.sharedrive.uam.product.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.service.AccountBasicConfigService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.product.domain.OrderBill;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.product.service.OrderBillService;
import com.huawei.sharedrive.uam.product.service.ProductService;
import com.huawei.sharedrive.uam.uservip.domain.EnterpriseVip;
import com.huawei.sharedrive.uam.uservip.service.EnterpriseVipService;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public class OrderProcessTask implements Runnable {

	private OrderBill orderBill;

	private long SPACE_QUOTA_VIP1 = 1073741824; // 默认1G大小

	private EnterpriseUserService enterpriseUserService;

	private EnterpriseAccountService enterpriseAccountService;

	private AccountBasicConfigService basicConfigService;

	private UserAccountManager userAccountManager;

	private ProductService productService;

	private OrderBillService orderBillService;

	private EnterpriseVipService enterpriseVipService;

	private static Logger logger = LoggerFactory.getLogger(OrderProcessTask.class);

	public OrderProcessTask(OrderBill orderBill, EnterpriseUserService enterpriseUserService,
			EnterpriseAccountService enterpriseAccountService, AccountBasicConfigService basicConfigService,
			UserAccountManager userAccountManager, OrderBillService orderBillService, ProductService productService,
			EnterpriseVipService enterpriseVipService) {
		this.orderBill = orderBill;
		this.enterpriseUserService = enterpriseUserService;
		this.enterpriseAccountService = enterpriseAccountService;
		this.basicConfigService = basicConfigService;
		this.userAccountManager = userAccountManager;
		this.orderBillService = orderBillService;
		this.productService = productService;
		this.enterpriseVipService = enterpriseVipService;

	}

	@Override
	public void run() {
		try {
			handleEnterpriseVip(orderBill);
			orderBill.setStatus(OrderBill.STATU_COMPLETE);
			orderBill.setFinishedDate(new Date());
			orderBillService.updateStatus(orderBill);

		} catch (Exception e) {

			orderBill.setStatus(OrderBill.STATU_FAILD);
			orderBillService.updateStatus(orderBill);
			logger.error(e.getMessage());
		}

	}


	private void handleEnterpriseVip(OrderBill orderBill) {

		switch (orderBill.getType()) {
			case 1:
				buyEnterpriseVip(orderBill);
				break;
			case 2:
				renewEnterpriseVip(orderBill);
				break;
			case 3:
				upgradeEnterpriseVip(orderBill);
				break;
	
			default:
				break;
			}

	}

	// 升级
	private void upgradeEnterpriseVip(OrderBill orderBill) {
		long enterpriseId = orderBill.getEnterpriseId();
		long accountId = orderBill.getAccountId();
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accountId);
		
		Calendar calendar = Calendar.getInstance();
		EnterpriseVip vipFilter = new EnterpriseVip();
		vipFilter.setEnterpriseAccountId(enterpriseAccount.getAccountId());
		vipFilter.setEnterpriseId(enterpriseId);
		EnterpriseVip enterpriseVip = enterpriseVipService.get(vipFilter);
		calendar.setTime(enterpriseVip.getExpireDate());
		calendar.add(Calendar.MONTH, (int) orderBill.getDuration());
		enterpriseVip.setExpireDate(calendar.getTime());
		enterpriseVipService.create(enterpriseVip);

		// 企业配置修改
		Product product = productService.get(orderBill.getProductId());
		long maxSpace = (product.getAccountNum() * product.getAccountSpace() + product.getTeamSpace());
		enterpriseAccount.setMaxMember(product.getAccountNum());
		enterpriseAccount.setMaxSpace(maxSpace);
		enterpriseAccountService.update(enterpriseAccount);

		// 新开户用户默认配置
		AccountBasicConfig filter = new AccountBasicConfig();
		filter.setAccountId(accountId);
		AccountBasicConfig basicConfig = basicConfigService.get(filter);
		if (basicConfig == null) {
			basicConfig = new AccountBasicConfig();
			basicConfig.setEnableTeamSpace(true);
			basicConfig.setTeamSpaceQuota(product.getTeamSpace() + "");
			basicConfig.setUserSpaceQuota(product.getAccountSpace() + "");
			basicConfig.setTeamSpaceVersions(AccountBasicConfig.DEFAULTVERSONS + "");
			basicConfig.setUserVersions(AccountBasicConfig.DEFAULTVERSONS + "");
			basicConfig.setAccountId(accountId);
			basicConfigService.insert(basicConfig);
		} else {
			basicConfig.setUserSpaceQuota(product.getAccountSpace() + "");
			basicConfig.setMaxTeamSpaces(product.getTeamNum() + "");
			basicConfigService.update(basicConfig);
		}

		// 修改已有成员配置
		List<EnterpriseUser> enterpriseUserlist = enterpriseUserService.getAllEnterpriseUser(enterpriseId);
		for (int i = 0; i < enterpriseUserlist.size(); i++) {

			UserAccount userAccount = userAccountManager.get(enterpriseUserlist.get(i).getId(), accountId);
			userAccount.setSpaceQuota(product.getAccountSpace());
			userAccountManager.update(userAccount, enterpriseAccount.getAuthAppId());
		}

	}

	// 续费
	private void renewEnterpriseVip(OrderBill orderBill) {
		long enterpriseId = orderBill.getEnterpriseId();
		long accountId = orderBill.getAccountId();
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accountId);
		Calendar calendar = Calendar.getInstance();
		EnterpriseVip filter = new EnterpriseVip();
		filter.setEnterpriseAccountId(enterpriseAccount.getAccountId());
		filter.setEnterpriseId(enterpriseId);
		EnterpriseVip enterpriseVip = enterpriseVipService.get(filter);
		calendar.setTime(enterpriseVip.getExpireDate());
		calendar.add(Calendar.MONTH, (int) orderBill.getDuration());
		enterpriseVip.setExpireDate(calendar.getTime());
		enterpriseVipService.create(enterpriseVip);

	}

	// 新购
	private void buyEnterpriseVip(OrderBill orderBill) {
		long enterpriseId = orderBill.getEnterpriseId();
		long accountId = orderBill.getAccountId();
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accountId);

		// 添加企业vip
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, (int) orderBill.getDuration());
		EnterpriseVip enterpriseVip = new EnterpriseVip();
		enterpriseVip.setEnterpriseAccountId(enterpriseAccount.getAccountId());
		enterpriseVip.setEnterpriseId(enterpriseId);
		enterpriseVip.setProductId(orderBill.getProductId());
		enterpriseVip.setStartDate(calendar.getTime());
		enterpriseVipService.create(enterpriseVip);

		// 企业配置修改
		Product product = productService.get(orderBill.getProductId());
		long maxSpace = (product.getAccountNum() * product.getAccountSpace() + product.getTeamSpace());
		enterpriseAccount.setMaxMember(product.getAccountNum());
		enterpriseAccount.setMaxSpace(maxSpace);
		enterpriseAccountService.update(enterpriseAccount);

		// 新开户用户默认配置
		AccountBasicConfig filter = new AccountBasicConfig();
		filter.setAccountId(accountId);
		AccountBasicConfig basicConfig = basicConfigService.get(filter);
		if (basicConfig == null) {
			basicConfig = new AccountBasicConfig();
			basicConfig.setEnableTeamSpace(true);
			basicConfig.setTeamSpaceQuota(product.getTeamSpace() + "");
			basicConfig.setUserSpaceQuota(product.getAccountSpace() + "");
			basicConfig.setTeamSpaceVersions(AccountBasicConfig.DEFAULTVERSONS + "");
			basicConfig.setUserVersions(AccountBasicConfig.DEFAULTVERSONS + "");
			basicConfig.setAccountId(accountId);
			basicConfigService.insert(basicConfig);
		} else {
			basicConfig.setUserSpaceQuota(product.getAccountSpace() + "");
			basicConfig.setMaxTeamSpaces(product.getTeamNum() + "");
			basicConfigService.update(basicConfig);
		}

		// 修改已有成员配置
		List<EnterpriseUser> enterpriseUserlist = enterpriseUserService.getAllEnterpriseUser(enterpriseId);
		for (int i = 0; i < enterpriseUserlist.size(); i++) {

			UserAccount userAccount = userAccountManager.get(enterpriseUserlist.get(i).getId(), accountId);
			userAccount.setSpaceQuota(product.getAccountSpace());
			userAccountManager.update(userAccount, enterpriseAccount.getAuthAppId());
		}

	}

}
