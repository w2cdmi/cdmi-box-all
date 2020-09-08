package com.huawei.sharedrive.uam.anon.domain;

/**
 * Created by hepan on 2017/4/28.
 */
public class RegisterEnterpriseParam {
    String name;
    String domainName;
    String accountId;
    String contactPerson;
    String contactPhone;
    String contactEmail;
    long maxSpace=-1;
    int maxMember=-1;
    int maxTeamspace=-1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public long getMaxSpace() {
        return maxSpace;
    }

    public void setMaxSpace(long maxSpace) {
        this.maxSpace = maxSpace;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public int getMaxTeamspace() {
        return maxTeamspace;
    }

    public void setMaxTeamspace(int maxTeamspace) {
        this.maxTeamspace = maxTeamspace;
    }
}
