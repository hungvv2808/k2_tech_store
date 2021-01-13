package vn.compedia.website.auction.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessJobRoot implements Serializable {

    @XmlElement(name="object")
    private List<BusinessJobElement> elements;

}
