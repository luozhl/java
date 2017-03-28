package cn.iot.m2m.gatewaybridge;

import java.io.Serializable;
import java.util.Date;

/**
 * GGSN报文数据对象
 */
public class CmSubsGprsStatus implements Serializable {

    private static final long serialVersionUID = 7221405650682675609L;

    private String msisdn;
    private String status;
    private Date statusDate;
    private String apnid;
    private String ip;
    private String accessType;
    private Date createDate;
    private long statusTimestamp;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getApnid() {
        return apnid;
    }

    public void setApnid(String apnid) {
        this.apnid = apnid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(long statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    @Override
    public String toString() {
        return "CmSubsGprsStatus{" +
                "msisdn='" + msisdn + '\'' +
                ", status='" + status + '\'' +
                ", statusDate=" + statusDate +
                ", apnid='" + apnid + '\'' +
                ", ip='" + ip + '\'' +
                ", accessType='" + accessType + '\'' +
                ", createDate=" + createDate +
                ", statusTimestamp=" + statusTimestamp +
                '}';
    }
}
