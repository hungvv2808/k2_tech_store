package vn.compedia.website.auction.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Getter
@Setter
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
public class SexElement implements Serializable {
    private String ma;
    private Integer id;
    private String ten;
}
