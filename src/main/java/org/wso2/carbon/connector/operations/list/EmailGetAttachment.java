/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.operations.list;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.exception.ContentBuilderException;
import org.wso2.carbon.connector.pojo.Attachment;
import org.wso2.carbon.connector.pojo.EmailMessage;
import org.wso2.carbon.connector.utils.ContentBuilder;
import org.wso2.carbon.connector.utils.EmailConstants;
import org.wso2.carbon.connector.utils.EmailPropertyNames;
import org.wso2.carbon.connector.utils.Error;
import org.wso2.carbon.connector.utils.ResponseHandler;

import java.util.List;

import static java.lang.String.format;

/**
 * Retrieves an email attachment
 */
public class EmailGetAttachment extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        String emailIndex = (String) getParameter(messageContext, EmailConstants.EMAIL_INDEX);
        String attachmentIndex = (String) getParameter(messageContext, EmailConstants.ATTACHMENT_INDEX);
        List<EmailMessage> emailMessages = (List<EmailMessage>) messageContext
                .getProperty(EmailPropertyNames.PROPERTY_EMAILS);

        if (emailIndex != null && attachmentIndex != null && emailMessages != null) {
            if (log.isDebugEnabled()) {
                log.debug(format("Retrieving email attachment for email at index %s and attachment at index %s...",
                        emailIndex, attachmentIndex));
            }
            try {
                EmailMessage emailMessage = getEmail(messageContext, emailMessages, emailIndex);
                if (emailMessage != null) {
                    Attachment attachment = getEmailAttachment(messageContext, emailMessage, attachmentIndex);
                    if (attachment != null) {
                        setProperties(messageContext, attachment);
                        ContentBuilder.buildContent(messageContext, attachment.getContent(),
                                attachment.getContentType());
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                setInvalidConfigurationError(messageContext,
                        format("Failed to retrieve attachment. %s", e.getMessage()), e);
            } catch (ContentBuilderException e) {
                handleException("Error occurred during setting attachment content.", e, messageContext);
            }
        } else if (emailIndex == null) {
            setInvalidConfigurationError(messageContext,
                    "Failed to retrieve attachment. Email Index is not set.");
        } else if (attachmentIndex == null) {
            setInvalidConfigurationError(messageContext,
                    "Failed to retrieve attachment. Attachment Index is not set.");
        } else {
            setInvalidConfigurationError(messageContext,
                    "Failed to retrieve attachment. No emails retrieved. " +
                            "Email list operation must be invoked first to retrieve emails.");
        }
    }

    /**
     * Gets email of respective index from list
     *
     * @param messageContext Message Context
     * @param emailMessages  List of Email Messages
     * @param emailIndex     Index of the email to be retrieved
     * @return Email message in the relevant index
     */
    private EmailMessage getEmail(MessageContext messageContext, List<EmailMessage> emailMessages, String emailIndex) {

        EmailMessage message = null;
        try {
            message = emailMessages.get(Integer.parseInt(emailIndex));
        } catch (IndexOutOfBoundsException e) {
            setInvalidConfigurationError(messageContext,
                    format("Failed to retrieve attachment. %s", e.getMessage()), e);
        }
        return message;
    }

    /**
     * Gets attachment of respective index from list
     *
     * @param messageContext  Message Context
     * @param emailMessage    Email message to retrieve attachment from
     * @param attachmentIndex Index of the attachment to be retrieved
     * @return Attachment in the relevant index
     */
    private Attachment getEmailAttachment(MessageContext messageContext, EmailMessage emailMessage,
                                          String attachmentIndex) {

        Attachment attachment = null;
        try {
            attachment = emailMessage.getAttachments().get(Integer.parseInt(attachmentIndex));
        } catch (IndexOutOfBoundsException e) {
            setInvalidConfigurationError(messageContext,
                    format("Failed to retrieve attachment. %s", e.getMessage()), e);
        }
        return attachment;
    }

    /**
     * Sets invalid configuration error
     *
     * @param messageContext Message Context
     * @param errorString    Error description
     */
    private void setInvalidConfigurationError(MessageContext messageContext, String errorString) {

        setInvalidConfigurationError(messageContext, errorString, null);
    }

    /**
     * Sets invalid configuration error
     *
     * @param messageContext Message Context
     * @param errorString    Error description
     */
    private void setInvalidConfigurationError(MessageContext messageContext, String errorString, Exception e) {

        ResponseHandler.setErrorsInMessage(messageContext, Error.INVALID_CONFIGURATION);
        handleException(errorString, e, messageContext);
    }

    /**
     * Sets attachment properties in Message Context
     *
     * @param messageContext Message Context
     * @param attachment     Attachment
     */
    private void setProperties(MessageContext messageContext, Attachment attachment) {

        messageContext.setProperty(EmailPropertyNames.PROPERTY_ATTACHMENT_TYPE, attachment.getContentType());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_ATTACHMENT_NAME, attachment.getName());
    }
}
