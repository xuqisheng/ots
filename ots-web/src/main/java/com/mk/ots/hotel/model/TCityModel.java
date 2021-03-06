package com.mk.ots.hotel.model;

import java.math.BigDecimal;

/**
 * TCityModel.
 * @author chuaiqing.
 *
 */
public class TCityModel {
    private Long cityid;

    private String code;

    private String cityname;

    private Integer proid;

    private Integer citysort;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String simplename;

    private String ishotcity;

    private Double range;

    private String isselect;

    private String querycityname;
    private Integer level;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }


    public Long getCityid() {
        return cityid;
    }

    public void setCityid(Long cityid) {
        this.cityid = cityid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname == null ? null : cityname.trim();
    }

    public Integer getProid() {
        return proid;
    }

    public void setProid(Integer proid) {
        this.proid = proid;
    }

    public Integer getCitysort() {
        return citysort;
    }

    public void setCitysort(Integer citysort) {
        this.citysort = citysort;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getSimplename() {
        return simplename;
    }

    public void setSimplename(String simplename) {
        this.simplename = simplename == null ? null : simplename.trim();
    }

    public String getIshotcity() {
        return ishotcity;
    }

    public void setIshotcity(String ishotcity) {
        this.ishotcity = ishotcity == null ? null : ishotcity.trim();
    }

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public String getIsselect() {
        return isselect;
    }

    public void setIsselect(String isselect) {
        this.isselect = isselect == null ? null : isselect.trim();
    }

    public String getQuerycityname() {
        return querycityname;
    }

    public void setQuerycityname(String querycityname) {
        this.querycityname = querycityname == null ? null : querycityname.trim();
    }
}