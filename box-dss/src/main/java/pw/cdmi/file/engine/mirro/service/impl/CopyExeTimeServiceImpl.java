package pw.cdmi.file.engine.mirro.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.file.engine.mirro.dao.CopyExeTimeDao;
import pw.cdmi.file.engine.mirro.domain.CopyExeTime;
import pw.cdmi.file.engine.mirro.service.CopyExeTimeService;

@Service("copyExeTimeService")
public class CopyExeTimeServiceImpl implements CopyExeTimeService
{ 
    @Autowired
    private CopyExeTimeDao copyExeTimeDao;
    @Override
    public void inserCopyExeRecord(CopyExeTime copyExe)
    {
        copyExeTimeDao.insertExeTimeRecord(copyExe);
    }
}
