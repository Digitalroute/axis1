/*
 * An XML document type.
 * Localname: echoStructParam
 * Namespace: http://soapinterop.org/xsd
 * Java type: org.soapinterop.xsd.EchoStructParamDocument
 *
 * Automatically generated - do not modify.
 */
package org.soapinterop.xsd;


/**
 * A document containing one echoStructParam(@http://soapinterop.org/xsd) element.
 *
 * This is a complex type.
 */
public interface EchoStructParamDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)schema.system.foo.TypeSystemHolder.typeSystem.resolveHandle("echostructparam5ef6doctype");
    
    /**
     * Gets the "echoStructParam" element
     */
    org.soapinterop.xsd.SOAPStruct getEchoStructParam();
    
    /**
     * Sets the "echoStructParam" element
     */
    void setEchoStructParam(org.soapinterop.xsd.SOAPStruct echoStructParam);
    
    /**
     * Appends and returns a new empty "echoStructParam" element
     */
    org.soapinterop.xsd.SOAPStruct addNewEchoStructParam();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.soapinterop.xsd.EchoStructParamDocument newInstance() {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        public static org.soapinterop.xsd.EchoStructParamDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.soapinterop.xsd.EchoStructParamDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}