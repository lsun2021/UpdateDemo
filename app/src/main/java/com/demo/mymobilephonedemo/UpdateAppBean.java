package com.demo.mymobilephonedemo;

import java.io.Serializable;

/**
 * @author: ${momoThree}
 * Date : 2017/10/24.
 * Title:
 */
public class UpdateAppBean  implements Serializable {
    /**
     * versionId : 2
     * versionNum : v10.0.2
     * status : 0
     * delFlag : 0
     * created : 1491790427
     * updated : 1491790427
     * downloadUrl : www.baidu.com
     * content : djfkdkkkk
     */

    private int versionId;
    private String versionNum;
    private int status;
    private int delFlag;
    private int created;
    private int updated;
    private String downloadUrl;
    private String content;

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}