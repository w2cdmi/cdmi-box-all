package com.huawei.sharedrive.app.filelabel.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.filelabel.dto.FileLabelRequestDto;
import com.huawei.sharedrive.app.filelabel.dto.FileMoveAndCopyDto;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.files.domain.INode;

/**
 * 
 * Desc  : 輔助工具類
 * Author: 77235
 * Date	 : 2016年12月17日
 */
public abstract class FilelabelUtils {    
    /**
     * 為新增的文件添加標簽信息
     * @param user
     * @param sourceNode
     * @param newNode
     */
    public static void bindFilelabelForNode(FileMoveAndCopyDto fmacDto, IFileLabelService filelabelService) {
        // 1、得到原文件編號綁定的標簽信息
        List<Long> flIds = filelabelService.retrivalFileLabelIdsByNode(fmacDto.getFormNodeId(), fmacDto.getFromOwnerId());
        
        // 2、為新node綁定標簽
        if (null != flIds && !flIds.isEmpty()) {
            List<String> labelIds = new ArrayList<String>();
            for (long fid : flIds) {
                labelIds.add(Long.valueOf(fid).toString());
            }
            
            List<FileLabel> filelabelList = filelabelService.retrivalFileLabelByIds(fmacDto.getEnterpriseId(), labelIds);
            for (FileLabel fl : filelabelList) {
                FileLabelRequestDto filelabelDto = new FileLabelRequestDto();
                filelabelDto.setLabelName(fl.getLabelName());
                filelabelDto.setNodeId(fmacDto.getDestNodeId());
                filelabelDto.setBindUserId(fmacDto.getOptUserId());
                filelabelDto.setOwnerId(fmacDto.getToOwnerId());
                filelabelDto.setBindType(fmacDto.getBindType().getBindingType());
                filelabelDto.setEnterpriseId(fmacDto.getEnterpriseId());
                
                filelabelService.bindFileLabel(filelabelDto);
            }
        }
    }
    
    
    /**
     * 为文件設置文件标签
     * @param fileList
     */
    public static void fillFilelabelForNode(long enterpriseId, INode node, IFileLabelService filelabelService) {        
        String flids = node.getFilelabelIds();
        
        if (StringUtils.isNotEmpty(flids)){
            String[] flidArr = flids.split(",");
            List<String> flidList = Arrays.asList(flidArr);
            
            List<FileLabel> fls = filelabelService.retrivalFileLabelByIds(enterpriseId, flidList);
            node.setFileLabelList(fls);
        }
    }
    
}
