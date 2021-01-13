package vn.compedia.website.auction.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class EducationXmlParseUtil {
    public static EducationRoot convertStringToXml(String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(EducationRoot.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (EducationRoot) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            return null;
        }
    }
}
