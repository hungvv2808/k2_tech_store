package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryRoot {
    @XmlElement(name="object")
    private List<CountryElement> elements;

    public List<CountryElement> getElements() {
        return elements;
    }

    public void setElements(List<CountryElement> elements) {
        this.elements = elements;
    }
}
