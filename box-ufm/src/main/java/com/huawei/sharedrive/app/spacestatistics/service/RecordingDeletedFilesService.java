package com.huawei.sharedrive.app.spacestatistics.service;

import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;

public interface RecordingDeletedFilesService
{
    void put(FilesDelete deletedFile);
}
