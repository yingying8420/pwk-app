package com.app.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Outlet{
    private String id;
    private String createUser;
    private String updateUser;
    private String createTime;
    private String updateTime;
    private Timestamp hcTime;
    private String riverid;
    private BigDecimal outletLongitude;
    private BigDecimal outletLatitude;
    private String outletSize;
    private String outletYesno;
    private String outletRhfs;
    private String outletPwlx;
    private String outletPwxz;
    private String outletType;
    private String routingType;
    private String outletCode;
    private String currOrgId;
    private String sourceType;
    private String outletPicname;
    private String picId;
    private String outletInfo;
    private String outletAddress;
    private String outletName;
    private String depname;
    private String deleteFlag;
    private String groupid;
    private String rivername;
    private String fullName;
    private List<String> imgGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getHcTime() {
        return hcTime;
    }

    public void setHcTime(Timestamp hcTime) {
        this.hcTime = hcTime;
    }

    public String getRiverid() {
        return riverid;
    }

    public void setRiverid(String riverid) {
        this.riverid = riverid;
    }

    public BigDecimal getOutletLongitude() {
        return outletLongitude;
    }

    public void setOutletLongitude(BigDecimal outletLongitude) {
        this.outletLongitude = outletLongitude;
    }

    public BigDecimal getOutletLatitude() {
        return outletLatitude;
    }

    public void setOutletLatitude(BigDecimal outletLatitude) {
        this.outletLatitude = outletLatitude;
    }

    public String getOutletSize() {
        return outletSize;
    }

    public void setOutletSize(String outletSize) {
        this.outletSize = outletSize;
    }

    public String getOutletYesno() {
        return outletYesno;
    }

    public void setOutletYesno(String outletYesno) {
        this.outletYesno = outletYesno;
    }

    public String getOutletRhfs() {
        return outletRhfs;
    }

    public void setOutletRhfs(String outletRhfs) {
        this.outletRhfs = outletRhfs;
    }

    public String getOutletPwlx() {
        return outletPwlx;
    }

    public void setOutletPwlx(String outletPwlx) {
        this.outletPwlx = outletPwlx;
    }

    public String getOutletPwxz() {
        return outletPwxz;
    }

    public void setOutletPwxz(String outletPwxz) {
        this.outletPwxz = outletPwxz;
    }

    public String getOutletType() {
        return outletType;
    }

    public void setOutletType(String outletType) {
        this.outletType = outletType;
    }

    public String getRoutingType() {
        return routingType;
    }

    public void setRoutingType(String routingType) {
        this.routingType = routingType;
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getCurrOrgId() {
        return currOrgId;
    }

    public void setCurrOrgId(String currOrgId) {
        this.currOrgId = currOrgId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getOutletPicname() {
        return outletPicname;
    }

    public void setOutletPicname(String outletPicname) {
        this.outletPicname = outletPicname;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getOutletInfo() {
        return outletInfo;
    }

    public void setOutletInfo(String outletInfo) {
        this.outletInfo = outletInfo;
    }

    public String getOutletAddress() {
        return outletAddress;
    }

    public void setOutletAddress(String outletAddress) {
        this.outletAddress = outletAddress;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getDepname() {
        return depname;
    }

    public void setDepname(String depname) {
        this.depname = depname;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getRivername() {
        return rivername;
    }

    public void setRivername(String rivername) {
        this.rivername = rivername;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getImgGroup() {
        return imgGroup;
    }

    public void setImgGroup(List<String> imgGroup) {
        this.imgGroup = imgGroup;
    }

    @Override
    public String toString() {
        return "Outlet{" +
                "id='" + id + '\'' +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", hcTime='" + hcTime + '\'' +
                ", riverid='" + riverid + '\'' +
                ", outletLongitude=" + outletLongitude +
                ", outletLatitude=" + outletLatitude +
                ", outletSize='" + outletSize + '\'' +
                ", outletYesno='" + outletYesno + '\'' +
                ", outletRhfs='" + outletRhfs + '\'' +
                ", outletPwlx='" + outletPwlx + '\'' +
                ", outletPwxz='" + outletPwxz + '\'' +
                ", outletType='" + outletType + '\'' +
                ", routingType='" + routingType + '\'' +
                ", outletCode='" + outletCode + '\'' +
                ", currOrgId='" + currOrgId + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", outletPicname='" + outletPicname + '\'' +
                ", picId='" + picId + '\'' +
                ", outletInfo='" + outletInfo + '\'' +
                ", outletAddress='" + outletAddress + '\'' +
                ", outletName='" + outletName + '\'' +
                ", depname='" + depname + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", groupid='" + groupid + '\'' +
                ", rivername='" + rivername + '\'' +
                ", fullName='" + fullName + '\'' +
                ", imgGroup=" + imgGroup +
                '}';
    }
}
