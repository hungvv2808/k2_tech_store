package vn.compedia.website.auction.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfficialElement implements Serializable {
    private String dienThoai;

    private String soCMND;

    private Long coQuanId;

    private String chucVu;

    private String noiCapCMND;

    private String coQuan;

    private String hoVaTen;

    private String chucVuId;

    private String ma;

//    private String imageUrl;

    private String ngaySinh;

    private Long id;

    private String email;

    private String ngayCapCMND;

}
