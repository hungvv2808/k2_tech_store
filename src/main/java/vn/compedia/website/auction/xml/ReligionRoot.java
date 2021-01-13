package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReligionRoot {

    @XmlElement(name="object")
    private List<ReligionElement> elements;

    public List<ReligionElement> getElements() {
        return elements;
    }

    public void setElements(List<ReligionElement> elements) {
        this.elements = elements;
    }
}
