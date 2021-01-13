package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegulatoryAgencyRoot implements Serializable {

    @XmlElement(name="object")
    private List<RegulatoryAgencyElement> elements;

    public List<RegulatoryAgencyElement> getElements() {
        return elements;
    }

    public void setElements(List<RegulatoryAgencyElement> elements) {
        this.elements = elements;
    }
}
