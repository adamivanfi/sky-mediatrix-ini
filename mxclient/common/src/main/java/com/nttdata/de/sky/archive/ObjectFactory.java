//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.30 at 09:39:11 AM MEZ 
//


package com.nttdata.de.sky.archive;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.math.BigInteger;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.cirquent.sky.archive package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _IsCampaign_QNAME = new QName("", "IsCampaign");
    private final static QName _Datadictionary_QNAME = new QName("", "datadictionary");
    private final static QName _Documentid_QNAME = new QName("", "documentid");
    private final static QName _Documentcount_QNAME = new QName("", "documentcount");
    private final static QName _Readablesource_QNAME = new QName("", "readablesource");
    private final static QName _Archivecommand_QNAME = new QName("", "archivecommand");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.cirquent.sky.archive
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Archive }
     * 
     */
    public Archive createArchive() {
        return new Archive();
    }

    /**
     * Create an instance of {@link Meta }
     * 
     */
    public Meta createMeta() {
        return new Meta();
    }

    /**
     * Create an instance of {@link Metavalue }
     * 
     */
    public Metavalue createMetavalue() {
        return new Metavalue();
    }

    /**
     * Create an instance of {@link Metarecord }
     * 
     */
    public Metarecord createMetarecord() {
        return new Metarecord();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "IsCampaign")
    public JAXBElement<String> createIsCampaign(String value) {
        return new JAXBElement<String>(_IsCampaign_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "datadictionary")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createDatadictionary(String value) {
        return new JAXBElement<String>(_Datadictionary_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "documentid")
    public JAXBElement<String> createDocumentid(String value) {
        return new JAXBElement<String>(_Documentid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "documentcount")
    public JAXBElement<BigInteger> createDocumentcount(BigInteger value) {
        return new JAXBElement<BigInteger>(_Documentcount_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "readablesource")
    public JAXBElement<String> createReadablesource(String value) {
        return new JAXBElement<String>(_Readablesource_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archivecommand")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createArchivecommand(String value) {
        return new JAXBElement<String>(_Archivecommand_QNAME, String.class, null, value);
    }

}