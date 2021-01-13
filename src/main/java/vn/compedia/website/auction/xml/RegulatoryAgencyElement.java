package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegulatoryAgencyElement implements Serializable {
    private Long capId;
    private String ma;
    private Long chaId;
    private Long id;
    private String ten;

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public Long getChaId() {
        return chaId;
    }

    public void setChaId(Long chaId) {
        this.chaId = chaId;
    }

    public Long getCapId() {
        return capId;
    }

    public void setCapId(Long capId) {
        this.capId = capId;
    }
}
