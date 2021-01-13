package vn.compedia.website.auction.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
public class CareerGroupElement implements Serializable {
    private Long cap;
    private String ma;
    private Long chaId;
    private Long id;
    private String ten;

}
