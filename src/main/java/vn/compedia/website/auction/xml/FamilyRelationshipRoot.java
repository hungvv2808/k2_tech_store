package vn.compedia.website.auction.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "objects")
@XmlAccessorType(XmlAccessType.FIELD)
public class FamilyRelationshipRoot {
    @XmlElement(name="object")
    private List<FamilyRelationshipElement> elements;

    public List<FamilyRelationshipElement> getElements() {
        return elements;
    }

    public void setElements(List<FamilyRelationshipElement> elements) {
        this.elements = elements;
    }
}
