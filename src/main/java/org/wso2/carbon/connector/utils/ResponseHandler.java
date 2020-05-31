package org.wso2.carbon.connector.utils;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.exception.ContentBuilderException;
import org.wso2.carbon.connector.pojo.EmailMessage;

import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import static java.lang.String.format;

/**
 * Generates responses
 */
public class ResponseHandler {

    private static final QName EMAILS_ELEMENT = new QName("emails");
    private static final QName EMAIL_ELEMENT = new QName("email");
    private static final QName ATTACHMENTS_ELEMENT = new QName("attachments");
    private static final QName ATTACHMENT_ELEMENT = new QName("attachment");
    private static final QName INDEX_ELEMENT = new QName("index");

    // Response constants
    private static final String START_TAG = "<result><success>";
    private static final String END_TAG = "</success></result>";

    private ResponseHandler() {

    }

    /**
     * Generates the output payload with result status
     *
     * @param messageContext The message context that is processed
     * @param resultStatus   Result of the status
     */
    public static void generateOutput(MessageContext messageContext, boolean resultStatus)
            throws ContentBuilderException {

        String response = START_TAG + resultStatus + END_TAG;
        ResponseHandler.preparePayload(messageContext, response);
    }

    /**
     * Sets payload in body
     *
     * @param messageContext The message context that is processed
     * @param output         Output response
     */
    public static void preparePayload(MessageContext messageContext, String output) throws ContentBuilderException {

        OMElement element;
        try {
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
            soapBody.addChild(element);
            messageContext.setResponse(true);
        } catch (XMLStreamException e) {
            throw new ContentBuilderException(format("Failed to set response in payload. %s", e.getMessage()), e);
        }
    }

    /**
     * Sets email response in body
     *
     * @param emailMessages  List of emails
     * @param messageContext The message context that is processed
     */
    public static void setEmailListResponse(List<EmailMessage> emailMessages, MessageContext messageContext) {

        org.apache.axis2.context.MessageContext axis2MsgCtx = ((org.apache.synapse.core.axis2.
                Axis2MessageContext) messageContext).getAxis2MessageContext();

        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        OMElement emailsElement = factory.createOMElement(EMAILS_ELEMENT);
        for (int i = 0; i < emailMessages.size(); i++) {
            OMElement emailElement = factory.createOMElement(EMAIL_ELEMENT);
            OMElement emailIndexElement = factory.createOMElement(INDEX_ELEMENT);
            emailIndexElement.addChild(factory.createOMText(Integer.toString(i)));
            emailElement.addChild(emailIndexElement);
            OMElement attachmentsElement = factory.createOMElement(ATTACHMENTS_ELEMENT);
            for (int j = 0; j < emailMessages.get(i).getAttachments().size(); j++) {
                OMElement attachmentElement = factory.createOMElement(ATTACHMENT_ELEMENT);
                OMElement attachmentIndexElement = factory.createOMElement(INDEX_ELEMENT);
                attachmentIndexElement.addChild(factory.createOMText(Integer.toString(j)));
                attachmentElement.addChild(attachmentIndexElement);
                attachmentsElement.addChild(attachmentElement);
            }
            emailElement.addChild(attachmentsElement);
            emailsElement.addChild(emailElement);
        }
        axis2MsgCtx.getEnvelope().getBody().addChild(emailsElement);
    }

    /**
     * Sets the error code and error detail in message
     *
     * @param messageContext Message Context
     * @param error          Error to be set
     */
    public static void setErrorsInMessage(MessageContext messageContext, Error error) {

        messageContext.setProperty(EmailPropertyNames.PROPERTY_ERROR_CODE, error.getErrorCode());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_ERROR_MESSAGE, error.getErrorDetail());
    }

}
