package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaperRoot {
    @XmlElement(name="object")
    private List<PaperElement> elements;

    public List<PaperElement> getElements() {
        return elements;
    }

    public void setElements(List<PaperElement> elements) {
        this.elements = elements;
    }
}
