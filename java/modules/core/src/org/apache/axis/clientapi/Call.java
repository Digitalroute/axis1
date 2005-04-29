package org.apache.axis.clientapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.addressing.EndpointReference;
import org.apache.axis.addressing.miheaders.RelatesTo;
import org.apache.axis.addressing.om.MessageInformationHeadersCollection;
import org.apache.axis.context.BasicMEPContext;
import org.apache.axis.context.EngineContext;
import org.apache.axis.context.MessageContext;
import org.apache.axis.context.ServiceContext;
import org.apache.axis.description.AxisGlobal;
import org.apache.axis.description.AxisOperation;
import org.apache.axis.description.AxisService;
import org.apache.axis.description.AxisTransportIn;
import org.apache.axis.description.AxisTransportOut;
import org.apache.axis.engine.AxisFault;
import org.apache.axis.engine.EngineConfiguration;
import org.apache.axis.engine.EngineConfigurationImpl;
import org.apache.axis.engine.MessageSender;
import org.apache.axis.om.OMException;
import org.apache.axis.om.SOAPEnvelope;
import org.apache.axis.transport.TransportReceiver;
import org.apache.axis.transport.TransportSender;

/**
 * Created by IntelliJ IDEA.
 * Author : Deepal Jayasinghe
 * Date: Apr 9, 2005
 * Time: 8:00:08 PM
 */
public class Call {

    private MessageInformationHeadersCollection messageInfoHeaders;

    private HashMap properties;

    private String transport = Constants.TRANSPORT_HTTP;

    private EngineContext engineContext;

    private EndpointReference replyTo;
    private String Listenertransport = Constants.TRANSPORT_HTTP;

    private boolean useSeparateListener = false;
    private String callbackServiceName;

    private CallbackReceiver callbackReceiver;

    private QName opName;
    
    private QName replyToOpName;
    private AxisOperation axisOperation;
    private AxisOperation callbackOperation;



    public Call() throws AxisFault {
        try {
            //find the deployment mechanism , create
            //a EngineContext .. if the conf file not found
            //deafult one is used
            properties = new HashMap();

            EngineConfiguration registry = new EngineConfigurationImpl(new AxisGlobal());

            //This is a hack, initialize the transports for the client side 
            AxisTransportOut httpTransportOut =
                new AxisTransportOut(new QName(Constants.TRANSPORT_HTTP));
            Class className = Class.forName("org.apache.axis.transport.http.HTTPTransportSender");
            httpTransportOut.setSender((TransportSender) className.newInstance());
            registry.addTransportOut(httpTransportOut);

            AxisTransportIn axisTr = new AxisTransportIn(new QName(Constants.TRANSPORT_HTTP));
            className = Class.forName("org.apache.axis.transport.http.HTTPTransportReceiver");
            axisTr.setReciver((TransportReceiver) className.newInstance());
            registry.addTransportIn(axisTr);

            AxisTransportOut mailTransportOut =
                new AxisTransportOut(new QName(Constants.TRANSPORT_MAIL));
            className = Class.forName("org.apache.axis.transport.mail.MailTransportSender");
            mailTransportOut.setSender((TransportSender) className.newInstance());
            registry.addTransportIn(new AxisTransportIn(new QName(Constants.TRANSPORT_MAIL)));
            registry.addTransportOut(mailTransportOut);

            this.engineContext = new EngineContext(registry);
            messageInfoHeaders = new MessageInformationHeadersCollection();
            init();
        } catch (ClassNotFoundException e) {
            throw new AxisFault(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new AxisFault(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new AxisFault(e.getMessage(), e);
        }
    }

    public Call(InputStream in) throws AxisFault {
        properties = new HashMap();
        //this.engineContext = new EngineContext();
        init();
        throw new UnsupportedOperationException("TODO fix this");
    }

    public Call(File inFile) throws AxisFault {
        try {
            InputStream in = new FileInputStream(inFile);

            properties = new HashMap();
            //this.engineContext = new EngineContext();
            init();
            throw new UnsupportedOperationException("TODO fix this");
        } catch (FileNotFoundException e) {
            throw new AxisFault("FileNotFound " + e.getMessage());
        }
    }

    public Call(EngineContext engineContext) {
        this.properties = new HashMap();
        this.engineContext = engineContext;
    }

    public void sendReceiveAsync(SOAPEnvelope env, final Callback callback) throws AxisFault {
        if(opName == null){
            throw new AxisFault("Operation Name must be specified");
        }
        
        EngineConfiguration registry = engineContext.getEngineConfig();
        if (Constants.TRANSPORT_MAIL.equals(transport)) {
            throw new AxisFault("This invocation support only for bi-directional transport");
        }
        try {
            MessageSender sender = new MessageSender(engineContext);

            final AxisTransportIn transportIn = registry.getTransportIn(new QName(transport));
            final AxisTransportOut transportOut = registry.getTransportOut(new QName(transport));

            final MessageContext msgctx =
                new MessageContext(
                    engineContext,
                    null,
                    null,
                    transportIn,
                    transportOut,
                    new BasicMEPContext(new AxisOperation(opName)));
                                
            msgctx.setEnvelope(env);

            if (useSeparateListener) {
                messageInfoHeaders.setMessageId(String.valueOf(System.currentTimeMillis()));
                callbackReceiver.addCallback(messageInfoHeaders.getMessageId(), callback);
                messageInfoHeaders.setReplyTo(
                    ListenerManager.replyToEPR(callbackServiceName + "/" + replyToOpName.getLocalPart()));
                callbackOperation.addMEPContext(msgctx.getMepContext(),messageInfoHeaders.getMessageId());
            }

            msgctx.setMessageInformationHeaders(messageInfoHeaders);

            sender.send(msgctx);

            //TODO start the server
            if (!useSeparateListener) {
                Runnable newThread = new Runnable() {
                    public void run() {
                        try {
                            MessageContext response =
                                new MessageContext(msgctx);
                            response.setServerSide(false);

                            TransportReceiver receiver = response.getTransportIn().getReciever();
                            receiver.invoke(response);
                            SOAPEnvelope resenvelope = response.getEnvelope();
                            AsyncResult asyncResult = new AsyncResult();
                            asyncResult.setResult(resenvelope);
                            callback.onComplete(asyncResult);
                        } catch (AxisFault e) {
                            callback.reportError(e);
                        }

                    }
                };
                (new Thread(newThread)).start();
            }

        } catch (OMException e) {
            throw AxisFault.makeFault(e);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public SOAPEnvelope sendReceiveSync(SOAPEnvelope env) throws AxisFault {
        if(opName == null){
            throw new AxisFault("Operation Name must be specified");
        }

        EngineConfiguration registry = engineContext.getEngineConfig();
        if (Constants.TRANSPORT_MAIL.equals(transport)) {
            throw new AxisFault("This invocation support only for bi-directional transport");
        }
        try {
            MessageSender sender = new MessageSender(engineContext);

            AxisTransportIn transportIn = registry.getTransportIn(new QName(transport));
            AxisTransportOut transportOut = registry.getTransportOut(new QName(transport));

            MessageContext msgctx =
                new MessageContext(
                    engineContext,
                    null,
                    null,
                    transportIn,
                    transportOut,
                    new BasicMEPContext(new AxisOperation(opName)));
            msgctx.setEnvelope(env);
            msgctx.setMessageInformationHeaders(messageInfoHeaders);

            sender.send(msgctx);

            MessageContext response = new MessageContext(msgctx);
            response.setServerSide(false);

            TransportReceiver receiver = response.getTransportIn().getReciever();
            receiver.invoke(response);
            SOAPEnvelope resenvelope = response.getEnvelope();

            // TODO if the resenvelope is a SOAPFault then throw an exception
            return resenvelope;
        } catch (OMException e) {
            throw AxisFault.makeFault(e);
        } catch (IOException e) {
            throw AxisFault.makeFault(e);
        }
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) throws AxisFault {
        if ((Constants.TRANSPORT_HTTP.equals(transport)
            || Constants.TRANSPORT_MAIL.equals(transport)
            || Constants.TRANSPORT_TCP.equals(transport))) {
            this.transport = transport;
        } else {
            throw new AxisFault("Selected transport dose not suppot ( " + transport + " )");
        }
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    private CallbackReceiver getService() {
        return null;

    }

    /**
     * This method is used to initilize the client side ,
     */
    private void init() throws AxisFault {
        messageInfoHeaders = new MessageInformationHeadersCollection();
        AxisService callbackService = new AxisService();
        callbackServiceName = CallbackReceiver.SERVIC_NAME + System.currentTimeMillis();
        callbackService.setName(new QName(callbackServiceName));
        callbackReceiver = new CallbackReceiver();
        callbackService.setMessageReceiver(callbackReceiver);

        replyToOpName = new QName("callback_op");
        callbackOperation = new AxisOperation(replyToOpName);
        callbackService.addOperation(callbackOperation);

        ListenerManager.makeSureStarted();

        ListenerManager.getEngineContext().addService(new ServiceContext(callbackService));

    }

    public void close() {
        ListenerManager.stopAServer();
    }

    /**
     * @return
     */
    public String getAction() {
        return messageInfoHeaders.getAction();
    }

    /**
     * @return
     */
    public EndpointReference getFaultTo() {
        return messageInfoHeaders.getFaultTo();
    }

    /**
     * @return
     */
    public EndpointReference getFrom() {
        return messageInfoHeaders.getFrom();
    }

    /**
     * @return
     */
    public String getMessageId() {
        return messageInfoHeaders.getMessageId();
    }

    /**
     * @return
     */
    public RelatesTo getRelatesTo() {
        return messageInfoHeaders.getRelatesTo();
    }

    /**
     * @return
     */
    public EndpointReference getReplyTo() {
        return messageInfoHeaders.getReplyTo();
    }

    /**
     * @return
     */
    public EndpointReference getTo() {
        return messageInfoHeaders.getTo();
    }

    /**
     * @param action
     */
    public void setAction(String action) {
        messageInfoHeaders.setAction(action);
    }

    /**
     * @param faultTo
     */
    public void setFaultTo(EndpointReference faultTo) {
        messageInfoHeaders.setFaultTo(faultTo);
    }

    /**
     * @param from
     */
    public void setFrom(EndpointReference from) {
        messageInfoHeaders.setFrom(from);
    }

    /**
     * @param messageId
     */
    public void setMessageId(String messageId) {
        messageInfoHeaders.setMessageId(messageId);
    }

    /**
     * @param relatesTo
     */

    public void setRelatesTo(RelatesTo relatesTo) {
        messageInfoHeaders.setRelatesTo(relatesTo);
    }

    /**
     * @param replyTo
     */
    public void setReplyTo(EndpointReference replyTo) {
        messageInfoHeaders.setReplyTo(replyTo);
    }

    /**
     * @param to
     */
    public void setTo(EndpointReference to) {
        messageInfoHeaders.setTo(to);
    }

    /**
     * todo
     * inoder to have asyn support for tansport , it shoud call this method
     *
     * @param Listenertransport
     * @param useSeparateListener
     * @throws AxisFault
     */
    public void setListenerTransport(String Listenertransport, boolean useSeparateListener)
        throws AxisFault {
        if ((Constants.TRANSPORT_HTTP.equals(Listenertransport)
            || Constants.TRANSPORT_MAIL.equals(Listenertransport)
            || Constants.TRANSPORT_TCP.equals(Listenertransport))) {
            this.Listenertransport = Listenertransport;
            this.useSeparateListener = useSeparateListener;
        } else {
            throw new AxisFault("Selected transport dose not suppot ( " + transport + " )");
        }
    }

    /**
     * @return
     */
    public QName getOpName() {
        return opName;
    }

    /**
     * @param name
     */
    public void setOpName(QName name) {
        opName = name;
    }

}
