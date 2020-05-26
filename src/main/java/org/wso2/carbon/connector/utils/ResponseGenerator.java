package org.wso2.carbon.connector.utils;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.AxisFault;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.builder.SOAPBuilder;
import org.apache.axis2.format.TextMessageBuilder;
import org.apache.axis2.format.TextMessageBuilderAdapter;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.base.SequenceMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.connector.connection.MailBoxConnection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;

public class ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResponseGenerator.class);

    /**
     * Prepare payload
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param output         Output response
     */
    public static void preparePayload(MessageContext messageContext, String output) throws XMLStreamException {
        OMElement element;
        if (StringUtils.isNotEmpty(output)) {
            element = AXIOMUtil.stringToOM(output);
        } else {
            element = AXIOMUtil.stringToOM("<result></></result>");
        }
        SOAPBody soapBody = messageContext.getEnvelope().getBody();
        for (Iterator itr = soapBody.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            child.detach();
        }
        for (Iterator itr = element.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            soapBody.addChild(child);
        }
        messageContext.setResponse(true);
    }

    public static void setResponseBody(String content, String contentType, MessageContext messageContext) throws AxisFault {
        org.apache.axis2.context.MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.
                Axis2MessageContext) messageContext).getAxis2MessageContext();
        Builder builder = BuilderUtil.getBuilderFromSelector(contentType, axis2MsgCtx);
        if (builder == null) {
            if (log.isDebugEnabled()) {
                log.debug("No message builder found for type '" + contentType + "'. Falling back to SOAP.");
            }
            builder = new SOAPBuilder();
        }
        OMElement documentElement;
        TextMessageBuilder textMessageBuilder = new TextMessageBuilderAdapter(builder);
        documentElement = textMessageBuilder.processDocument(content, contentType, axis2MsgCtx);
        axis2MsgCtx.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));
    }
}
