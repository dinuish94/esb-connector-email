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
import org.wso2.carbon.connector.pojo.EmailMessage;
import org.wso2.carbon.connector.utils.EmailConstants;
import org.wso2.carbon.connector.utils.EmailPropertyNames;
import org.wso2.carbon.connector.utils.Error;
import org.wso2.carbon.connector.utils.ResponseHandler;

import java.util.List;

import static java.lang.String.format;

/**
 * Retrieves an email body
 */
public class EmailGetBody extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        String emailIndex = (String) getParameter(messageContext, EmailConstants.EMAIL_INDEX);
        String errorString = "Error occurred while retrieving email body.";
        List<EmailMessage> emailMessages = (List<EmailMessage>) messageContext
                .getProperty(EmailPropertyNames.PROPERTY_EMAILS);

        if (emailIndex != null && emailMessages != null) {
            EmailMessage emailMessage = getEmail(messageContext, emailMessages, emailIndex);
            if (emailMessage != null) {
                if (log.isDebugEnabled()) {
                    log.debug(format("Retrieving email body for email at index %s...", emailIndex));
                }
                setProperties(messageContext, emailMessage);
            }
        } else if (emailIndex == null) {
            setInvalidConfigurationError(messageContext, format("%s Email Index is not set.", errorString));
        } else {
            setInvalidConfigurationError(messageContext,format("%s No emails retrieved. " +
                    "Email list operation must be invoked first to retrieve emails.", errorString));
        }
    }

    /**
     * Sets email message content in Message Context
     *
     * @param messageContext Message Context
     * @param emailMessage   Email
     */
    private void setProperties(MessageContext messageContext, EmailMessage emailMessage) {

        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_ID, emailMessage.getEmailId());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_TO, emailMessage.getTo());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_FROM, emailMessage.getFrom());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_CC, emailMessage.getCc());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_BCC, emailMessage.getBcc());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_SUBJECT, emailMessage.getSubject());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_REPLY_TO, emailMessage.getReplyTo());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_HTML_CONTENT, emailMessage.getHtmlContent());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_TEXT_CONTENT, emailMessage.getTextContent());
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
}
