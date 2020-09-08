package pw.cdmi.file.engine.filesystem.model;

import pw.cdmi.common.log.LogFormat;
import pw.cdmi.file.engine.core.common.CloneableEntity;
import pw.cdmi.file.engine.core.ibatis.Namingspace;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

@Namingspace("fsEndpoint")
public class FSEndpoint extends CloneableEntity implements Serializable, LogFormat {
	private static final long serialVersionUID = 7471417624053548979L;
	private String id;			//挂载资源空间的ID
	private String endpoint;	//挂载资源空间的endpoint信息，包含了访问协议，访问路径，访问端口，访问授权信息
	private String fsType;		//挂载资源空间的类型
	private boolean writeAble = true;	//挂载资源空间是否可写

	private boolean noSpace = false;

	private boolean available = true;

	private Float maxUtilization = null;

	private Float retrieval = null;

	private long spaceSize = 0L;		//挂载资源空间的存储空间大小

	private long usedSize = 0L;			//挂载资源空间的已使用存储空间大小

	private int priority = 1;

	private FSEndpointStatus status = FSEndpointStatus.NOT_ENABLED;

	private List<FSAccessPath> accessPaths = new ArrayList<FSAccessPath>(1);
	private String device;
	private Long bestStartRange = Long.valueOf(0L);

	private Long bestEndRange = Long.valueOf(-1L);

	private Boolean multipartFirst = Boolean.valueOf(false);

	public FSEndpoint createRealFSEndpoint() {
		FSDefinition fsDefinition = FSDefinition.findFSDefinition(getFsType());
		FSEndpoint endpoint = fsDefinition.createFSEndpointInstants();
		endpoint.setId(getId());
		endpoint.setEndpoint(getEndpoint());
		endpoint.setWriteAble(isWriteAble());
		endpoint.setNoSpace(isNoSpace());
		endpoint.setAvailable(isAvailable());
		endpoint.setMaxUtilization(getMaxUtilization());
		endpoint.setRetrieval(getRetrieval());
		endpoint.setSpaceSize(getSpaceSize());
		endpoint.setUsedSize(getUsedSize());
		endpoint.setPriority(getPriority());
		endpoint.setStatus(getStatus());
		endpoint.setDevice(getDevice());
		endpoint.getAccessPaths().addAll(getAccessPaths());
		endpoint.setBestStartRange(getBestStartRange());
		endpoint.setBestEndRange(getBestEndRange());
		endpoint.setMultipartFirst(getMultipartFirst());
		return endpoint;
	}

	public FSEndpoint() {
	}

	public void refreshSpaceInfo(long totalSize, long usedSize) {
		setSpaceSize(totalSize);
		setUsedSize(usedSize);

		float utilization = computeUtilization(usedSize, totalSize);

		if (utilization >= getMaxUtilization().floatValue()) {
			setNoSpace(true);
		}

		if (utilization <= getRetrieval().floatValue()) {
			setNoSpace(false);
		}
	}

	public FSEndpoint(String id) {
		this.id = id;
	}

	public FSEndpoint(String id, String device) {
		this.id = id;
		this.device = device;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getFsType() {
		return StringUtils.lowerCase(this.fsType);
	}

	public void setFsType(String fsType) {
		this.fsType = StringUtils.lowerCase(fsType);
	}

	public boolean isReadOnly() {
		return (isNoSpace()) || (!isWriteAble()) || (!FSEndpointStatus.ENABLE.equals(getStatus()));
	}

	public Float getMaxUtilization() {
		return this.maxUtilization;
	}

	public void setMaxUtilization(Float maxUtilization) {
		this.maxUtilization = maxUtilization;
	}

	public boolean isWriteAble() {
		return this.writeAble;
	}

	public void setWriteAble(boolean writeAble) {
		this.writeAble = writeAble;
	}

	public boolean isNoSpace() {
		return this.noSpace;
	}

	public void setNoSpace(boolean noSpace) {
		this.noSpace = noSpace;
	}

	public boolean isAvailable() {
		return this.available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Float getRetrieval() {
		return this.retrieval;
	}

	public void setRetrieval(Float retrieval) {
		this.retrieval = retrieval;
	}

	public long getSpaceSize() {
		return this.spaceSize;
	}

	public void setSpaceSize(long spaceSize) {
		this.spaceSize = spaceSize;
	}

	public long getUsedSize() {
		return this.usedSize;
	}

	public void setUsedSize(long usedSize) {
		this.usedSize = usedSize;
	}

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<FSAccessPath> getAccessPaths() {
		if (this.accessPaths == null) {
			this.accessPaths = new ArrayList<FSAccessPath>(1);
		}
		return this.accessPaths;
	}

	public void setAccessPaths(List<FSAccessPath> accessPaths) {
		this.accessPaths = accessPaths;
	}

	public void addAccessPaths(FSAccessPath accessPath) {
		getAccessPaths().add(accessPath);
	}

	public FSEndpointStatus getStatus() {
		return this.status;
	}

	public void setStatus(FSEndpointStatus status) {
		this.status = status;
	}

	public FSAccessPathSelector getFsAccessPathManager() {
		throw new UnsupportedOperationException();
	}

	public String getDevice() {
		return this.device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public long getMaxAvailSize() {
		if (0L == getSpaceSize()) {
			return 9223372036854775807L;
		}

		BigDecimal space = new BigDecimal(getSpaceSize());
		BigDecimal maxUtilization;
		if (getMaxUtilization() == null) {
			maxUtilization = BigDecimal.ONE;
		} else {
			maxUtilization = new BigDecimal(getMaxUtilization().floatValue()).divide(new BigDecimal(100), 4, 4);
		}

		long maxAvail = space.multiply(maxUtilization).longValue();

		return maxAvail - getUsedSize();
	}

	public Long getBestStartRange() {
		return this.bestStartRange;
	}

	public void setBestStartRange(Long bestStartRange) {
		this.bestStartRange = bestStartRange;
	}

	public Long getBestEndRange() {
		return this.bestEndRange;
	}

	public void setBestEndRange(Long bestEndRange) {
		this.bestEndRange = bestEndRange;
	}

	public Boolean getMultipartFirst() {
		return this.multipartFirst;
	}

	public void setMultipartFirst(Boolean multipartFirst) {
		this.multipartFirst = multipartFirst;
	}

	public String logFormat() {
		StringBuilder sb = new StringBuilder(FSEndpoint.class.getCanonicalName()).append("[").append("id=")
				.append(this.id).append(", ").append("fsType=").append(this.fsType).append(", ").append("isAvailable=")
				.append(this.available).append(", ").append("isWriteAble=").append(this.writeAble).append(", ")
				.append("isNoSpace=").append(this.noSpace).append(", ").append("maxUtilization=")
				.append(this.maxUtilization).append(", ").append("retrieval=").append(this.retrieval).append(", ")
				.append("status=").append(this.status).append("]");
		return sb.toString();
	}

	private float computeUtilization(long usedSize, long totalSize) {
		BigDecimal used = new BigDecimal(usedSize);
		BigDecimal total = new BigDecimal(totalSize);

		return used.floatValue() / total.floatValue() * 100.0F;
	}
}