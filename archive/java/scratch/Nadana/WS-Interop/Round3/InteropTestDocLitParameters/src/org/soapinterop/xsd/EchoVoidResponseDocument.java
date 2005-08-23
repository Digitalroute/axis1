/*
 * An XML document type.
 * Localname: echoVoidResponse
 * Namespace: http://soapinterop.org/xsd
 * Java type: org.soapinterop.xsd.EchoVoidResponseDocument
 *
 * Automatically generated - do not modify.
 */
package org.soapinterop.xsd;


/**
 * A document containing one echoVoidResponse(@http://soapinterop.org/xsd) element.
 *
 * This is a complex type.
 */
public interface EchoVoidResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)schema.system.foo.TypeSystemHolder.typeSystem.resolveHandle("echovoidresponsef4dddoctype");
    
    /**
     * Gets the "echoVoidResponse" element
     */
    org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse getEchoVoidResponse();
    
    /**
     * Sets the "echoVoidResponse" element
     */
    void setEchoVoidResponse(org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse echoVoidResponse);
    
    /**
     * Appends and returns a new empty "echoVoidResponse" element
     */
    org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse addNewEchoVoidResponse();
    
    /**
     * An XML echoVoidResponse(@http://soapinterop.org/xsd).
     *
     * This is a complex type.
     */
    public interface EchoVoidResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)schema.system.foo.TypeSystemHolder.typeSystem.resolveHandle("echovoidresponse8bb3elemtype");
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse newInstance() {
              return (org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.soapinterop.xsd.EchoVoidResponseDocument.EchoVoidResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.soapinterop.xsd.EchoVoidResponseDocument newInstance() {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        public static org.soapinterop.xsd.EchoVoidResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.soapinterop.xsd.EchoVoidResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}