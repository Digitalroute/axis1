/*
 * Created on Apr 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.axis.saaj;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis.transport.http.HTTPConstants;

/**
 * @author Ashutosh Shahi ashutosh.shahi@gmail.com
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SOAPMessageImpl extends SOAPMessage {
	
	private SOAPPartImpl mSOAPPart;
    private java.util.Hashtable mProps = new java.util.Hashtable();
    private MimeHeaders headers;

	public SOAPMessageImpl(Object initialContents){
		try{
		setup(initialContents, false, null, null, null);
		} catch(SOAPException e){
			e.printStackTrace();
		}
	}
	
    public SOAPMessageImpl(Object initialContents, boolean bodyInStream, javax.xml.soap.MimeHeaders headers) {
    	try{
    	setup(initialContents, bodyInStream, null, null, (MimeHeaders)headers);
		} catch(SOAPException e){
			e.printStackTrace();
		}   	
    }
	
	private void setup(Object initialContents, boolean bodyInStream,
			String contentType, String contentLocation,
			MimeHeaders mimeHeaders)throws SOAPException{
		if(null == mSOAPPart)
			mSOAPPart = new SOAPPartImpl(this, initialContents, bodyInStream);
		else
			mSOAPPart.setMessage(this);
		
		headers = (mimeHeaders == null) ? new MimeHeaders() : new MimeHeaders(mimeHeaders);
	}
	
    /**
     * Retrieves a description of this <CODE>SOAPMessage</CODE>
     * object's content.
     * @return  a <CODE>String</CODE> describing the content of this
     *     message or <CODE>null</CODE> if no description has been
     *     set
     * @see #setContentDescription(java.lang.String) setContentDescription(java.lang.String)
     */
    public String getContentDescription() {
        String values[] = headers.getHeader(HTTPConstants.HEADER_CONTENT_DESCRIPTION);
        if(values != null && values.length > 0)
            return values[0];
        return null;
    }

    /**
     * Sets the description of this <CODE>SOAPMessage</CODE>
     * object's content with the given description.
     * @param  description a <CODE>String</CODE>
     *     describing the content of this message
     * @see #getContentDescription() getContentDescription()
     */
    public void setContentDescription(String description) {
        headers.setHeader(HTTPConstants.HEADER_CONTENT_DESCRIPTION, description);
    }

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#getSOAPPart()
	 */
	public SOAPPart getSOAPPart() {
		return mSOAPPart;
	}
	
    public SOAPBody getSOAPBody() throws SOAPException {
        return mSOAPPart.getEnvelope().getBody();
    }

    public SOAPHeader getSOAPHeader() throws SOAPException {
        return mSOAPPart.getEnvelope().getHeader();
    }
    
    public void setProperty(String property, Object value) throws SOAPException {
        mProps.put(property, value);
    }

    public Object getProperty(String property) throws SOAPException {
        return mProps.get(property);
    }

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#removeAllAttachments()
	 */
	public void removeAllAttachments() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#countAttachments()
	 */
	public int countAttachments() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#getAttachments()
	 */
	public Iterator getAttachments() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#getAttachments(javax.xml.soap.MimeHeaders)
	 */
	public Iterator getAttachments(javax.xml.soap.MimeHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#addAttachmentPart(javax.xml.soap.AttachmentPart)
	 */
	public void addAttachmentPart(AttachmentPart attachmentpart) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#createAttachmentPart()
	 */
	public AttachmentPart createAttachmentPart() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#getMimeHeaders()
	 */
	public javax.xml.soap.MimeHeaders getMimeHeaders() {
		
		return headers;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#saveChanges()
	 */
	public void saveChanges() throws SOAPException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#saveRequired()
	 */
	public boolean saveRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.xml.soap.SOAPMessage#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream out) throws SOAPException, IOException {
		try{
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
		((SOAPEnvelopeImpl)mSOAPPart.getEnvelope()).getOMEnvelope().serialize(writer, true);
		writer.flush();
		} catch(Exception e){
			throw new SOAPException(e);
		}
	}

}