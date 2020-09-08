package com.huawei.sharedrive.uam.cmb.oa.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.datacontract.schemas._2004._07.cmbchina_yst.OrgInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.OrgUserRelationInfo;
import org.datacontract.schemas._2004._07.cmbchina_yst.SapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.cmb.control.manager.Constants;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.service.ConvertFromWsdl;
import com.huawei.sharedrive.uam.cmb.oa.util.CMBOAUtil;

@Service
public class ConvertFromWsdlImpl implements ConvertFromWsdl
{
    private static Logger LOGGER = LoggerFactory.getLogger(ConvertFromWsdlImpl.class);
    
    @Override
    public List<CMBOrgInfo> convertToCMBOrgInfo(List<OrgInfo> infos)
    {
        if (null == infos || infos.size() < 1)
        {
            return null;
        }
        List<CMBOrgInfo> cmbOrgInfos = new ArrayList<CMBOrgInfo>();
        
        for (OrgInfo info : infos)
        {
            CMBOrgInfo cmbOrgInfo = convertOrgInfo(info);
            if (null == cmbOrgInfo)
            {
                continue;
            }
            cmbOrgInfos.add(cmbOrgInfo);
            
        }
        return cmbOrgInfos;
        
    }
    
    @Override
    public List<CMBSapUser> convertToCMBSapUser(List<SapUser> users)
    {
        if (null == users || users.size() < 1)
        {
            return null;
        }
        
        List<CMBSapUser> sapUsers = new ArrayList<CMBSapUser>();
        
        for (SapUser user : users)
        {
            CMBSapUser cmbSapUser = convertSapUser(user);
            if (null == cmbSapUser)
            {
                continue;
            }
            sapUsers.add(cmbSapUser);
        }
        return sapUsers;
        
    }
    
    @Override
    public List<CMBUserRelationInfo> convertToCMBUserRelationInfo(
        List<OrgUserRelationInfo> orgUserRelationInfos)
    {
        if (null == orgUserRelationInfos || orgUserRelationInfos.size() < 1)
        {
            return null;
        }
        
        List<CMBUserRelationInfo> relationInfos = new ArrayList<CMBUserRelationInfo>();
        for (OrgUserRelationInfo org : orgUserRelationInfos)
        {
            CMBUserRelationInfo cmbUserRelationInfo = convertOrgUserRelationInfo(org);
            if (null == cmbUserRelationInfo)
            {
                continue;
            }
            relationInfos.add(cmbUserRelationInfo);
        }
        return relationInfos;
    }
    
    private CMBOrgInfo convertOrgInfo(OrgInfo orgInfo)
    {
        CMBOrgInfo cmbOrgInfo = new CMBOrgInfo();
        cmbOrgInfo.setOrgId(orgInfo.getORGID());
        cmbOrgInfo.setGroupId(orgInfo.getGROUPID().getValue());
        cmbOrgInfo.setGroupName(orgInfo.getGROUPNAME().getValue());
        String fatherGroupId = orgInfo.getFATHERGROUPID() == null ? null : orgInfo.getFATHERGROUPID()
            .getValue();
        cmbOrgInfo.setFatherGroupId(fatherGroupId);
        cmbOrgInfo.setFatherOrgId(orgInfo.getFOURCLASSORGID());
        cmbOrgInfo.setLeaf(orgInfo.getLEAF());
        cmbOrgInfo.setUnitType(orgInfo.getUNITTYPE());
        cmbOrgInfo.setHierarchyflag(orgInfo.getHIERARCHYFLAG());
        String emaiId = orgInfo.getEMAILID() == null ? null : orgInfo.getEMAILID().getValue();
        cmbOrgInfo.setEmailId(emaiId);
        String headerId = orgInfo.getHEADERID() == null ? null : orgInfo.getHEADERID().getValue();
        cmbOrgInfo.setHeaderId(headerId);
        cmbOrgInfo.setDisplay(orgInfo.getDISPLAY());
        cmbOrgInfo.setPoint(orgInfo.getPOINT());
        String pathName = orgInfo.getPATHNAME() == null ? null : orgInfo.getPATHNAME().getValue();
        cmbOrgInfo.setPathName(pathName);
        cmbOrgInfo.setOneclassOrgId(orgInfo.getONECLASSORGID());
        cmbOrgInfo.setTwoClassOrgId(orgInfo.getTWOCLASSORGID());
        cmbOrgInfo.setThreeClassOrgId(orgInfo.getTHREECLASSORGID());
        cmbOrgInfo.setFourClassOrgId(orgInfo.getFOURCLASSORGID());
        cmbOrgInfo.setFiveClassOrgId(orgInfo.getFIVECLASSORGID());
        cmbOrgInfo.setUsePlace(orgInfo.getUSEPLACE());
        cmbOrgInfo.setGroupProperty(orgInfo.getGROUPPROPERTY());
        cmbOrgInfo.setLocation(orgInfo.getLOCATION());
        cmbOrgInfo.setSortId(orgInfo.getSORTID());
        String exInfo = orgInfo.getEXINFO() == null ? null : orgInfo.getEXINFO().getValue();
        cmbOrgInfo.setExInfo(exInfo);
        return cmbOrgInfo;
    }
    
    private CMBSapUser convertSapUser(SapUser user)
    {
        CMBSapUser tempUser = new CMBSapUser();
        String userId = getElementStr(user.getUSERID());
        if (StringUtils.isBlank(userId))
        {
            LOGGER.error("user id is null");
            return null;
        }
        tempUser.setUserId(userId);
        
        String name = getElementStr(user.getNAME());
        if (StringUtils.isBlank(name))
        {
            LOGGER.error("name is null userId:" + userId);
            return null;
        }
        tempUser.setName(name);
        
        tempUser.setGender(user.getGENDER());
        String officeTel = user.getOFFICETEL() == null ? null : user.getOFFICETEL().getValue();
        tempUser.setOfficeTel(officeTel);
        
        String sapId = getElementStr(user.getSAPID());
        if (StringUtils.isBlank(sapId))
        {
            LOGGER.error("sapId is null userId:" + userId + " name:" + name);
            return null;
        }
        tempUser.setSapId(sapId);
        
        String position = user.getPOSITION() == null ? null : user.getPOSITION().getValue();
        tempUser.setPosition(position);
        
        String status = getElementStr(user.getSTATUS());
        if (StringUtils.isBlank(status))
        {
            LOGGER.error("status is  userId:" + userId + " name:" + name);
            return null;
        }
        tempUser.setStatus(status);
        
        String email = getElementStr(user.getEMAIL());
        if (StringUtils.isBlank(email))
        {
            LOGGER.error("email is  userId:" + userId + " name:" + name + " sapId" + sapId);
            return null;
        }
        tempUser.setEmail(email + Constants.EMAIL_SUFFIX);
        
        tempUser.setType(user.getTYPE());
        tempUser.setFirstSpell(CMBOAUtil.convertToFirstSpell(name));
        tempUser.setSpell(CMBOAUtil.convertToSpell(name));
        return tempUser;
    }
    
    private CMBUserRelationInfo convertOrgUserRelationInfo(OrgUserRelationInfo org)
    {
        CMBUserRelationInfo info = new CMBUserRelationInfo();
        String groupId = getElementStr(org.getGROUPID());
        if (StringUtils.isBlank(groupId))
        {
            LOGGER.error("groupId is null");
            return null;
        }
        info.setGroupId(groupId);
        
        String name = getElementStr(org.getNAME());
        if (StringUtils.isBlank(name))
        {
            LOGGER.error("name is null");
            return null;
        }
        info.setName(name);
        
        info.setOrgId(org.getORGID());
        
        String userId = getElementStr(org.getUSERID());
        if (StringUtils.isBlank(userId))
        {
            LOGGER.error("userId is null");
            return null;
        }
        info.setUserId(userId);
        
        info.setUserOrd(org.getUSERORD());
        return info;
    }
    
    private String getElementStr(JAXBElement<String> element)
    {
        if (null == element)
        {
            return null;
        }
        return element.getValue();
    }
    
}
