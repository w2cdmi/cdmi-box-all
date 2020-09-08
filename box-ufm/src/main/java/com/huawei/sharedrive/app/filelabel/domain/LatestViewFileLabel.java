package com.huawei.sharedrive.app.filelabel.domain;

import java.io.Serializable;

/**
 * 
 * Desc  : 用户最近访问标签
 * Author: 77235
 * Date	 : 2016年12月2日
 */
public class LatestViewFileLabel extends BaseFileLabelInfo implements Comparable<LatestViewFileLabel>,Serializable{

    private static final long serialVersionUID = 1L;
    /** 访问时间 */
    private long visitedTime;

    public long getVisitedTime() {
        return visitedTime;
    }

    public void setVisitedTime(long visitedTime) {
        this.visitedTime = visitedTime;
    }
    
    public LatestViewFileLabel() {
    	super();
    }

    public LatestViewFileLabel(long id, String labelName) {
        super(id, labelName);
        visitedTime = System.currentTimeMillis();
    }


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LatestViewFileLabel other = (LatestViewFileLabel) obj;
		if (visitedTime != other.visitedTime)
			return false;
		return true;
	}

	@Override
    public int compareTo(LatestViewFileLabel other) {
        
        return (int)(this.visitedTime - other.visitedTime);
    }
}
