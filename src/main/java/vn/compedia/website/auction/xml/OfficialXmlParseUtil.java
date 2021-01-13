package vn.compedia.website.auction.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class OfficialXmlParseUtil {
    public static OfficialsRoot convertStringToXml(String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(OfficialsRoot.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (OfficialsRoot) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            return null;
        }
    }
}
