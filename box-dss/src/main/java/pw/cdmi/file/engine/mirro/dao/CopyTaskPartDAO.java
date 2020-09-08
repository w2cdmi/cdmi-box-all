package pw.cdmi.file.engine.mirro.dao;

import java.util.List;

import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;

public interface CopyTaskPartDAO
{
    int countCopyTaskPart(String paramString);
    
    void batchReplace(List<CopyTaskPart> paramList);
    
    List<CopyTaskPart> listCopyTaskPartByTaskId(String paramString);
    
    int countUnSuccessTaskParts(String paramString);
    
    void updateTaskPartsStatus(CopyTaskPart paramList);
    
    void deleteByTaskId(String taskId);

    void deleteUnExistTaskPart();
}