package arkuni.util.hostfile;

public class HostInfoBean implements HostInfo{
	private String ipInfo;
	private String hostInfo;
	private String etcInfo;
	private boolean isActive = true;
	private int lineNo = 0;
	private String alias;
	
	public String getIpInfo() {
		return ipInfo;
	}
	public void setIpInfo(String ipInfo) {
		this.ipInfo = ipInfo;
	}
	public String getHostInfo() {
		return hostInfo;
	}
	public void setHostInfo(String hostInfo) {
		this.hostInfo = hostInfo;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public int getInfoType() {
		return HOSTINFO;
	}
	public String getEtcInfo() {
		return etcInfo;
	}
	public void setEtcInfo(String etcInfo) {
		this.etcInfo = etcInfo;
	}
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	@Override
	public String toString() {
		return "ipInfo=" + this.ipInfo + "&hostInfo="+ this.hostInfo + "&etcInfo="+ this.etcInfo + "&isActive="+ this.isActive + "&alias="+ this.alias;
	}
}
