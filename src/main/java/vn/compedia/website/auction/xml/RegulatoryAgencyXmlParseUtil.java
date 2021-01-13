package vn.compedia.website.auction.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class RegulatoryAgencyXmlParseUtil {
    public static RegulatoryAgencyRoot convertStringToXml(String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(RegulatoryAgencyRoot.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (RegulatoryAgencyRoot) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            return null;
        }
    }
}
