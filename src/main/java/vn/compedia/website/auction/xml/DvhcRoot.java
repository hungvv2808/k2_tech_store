package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class DvhcRoot implements Serializable {

    @XmlElement(name="object")
    private List<DvhcElement> elements;

    public List<DvhcElement> getElements() {
        return elements;
    }

    public void setElements(List<DvhcElement> elements) {
        this.elements = elements;
    }
}
