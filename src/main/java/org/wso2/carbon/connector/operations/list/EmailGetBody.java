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

import java.util.List;

import static java.lang.String.format;

/**
 * Retrieves an email body
 */
public class EmailGetBody extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext){
        String emailIndex = (String) getParameter(messageContext, EmailConstants.EMAIL_INDEX);
        List<EmailMessage> emailMessages = (List<EmailMessage>) messageContext
                .getProperty(EmailPropertyNames.PROPERTY_EMAILS);

        if (emailIndex != null && emailMessages != null) {
            EmailMessage emailMessage = emailMessages.get(Integer.parseInt(emailIndex));
            if (log.isDebugEnabled()) {
                log.debug(format("Retrieving email body for email at index %s...", emailIndex));
            }
            setProperties(messageContext, emailMessage);
        } else if (emailIndex == null) {
            log.error("Failed to retrieve email body. Email Index is not set.");
        } else {
            log.error("Failed to retrieve email body. No emails retrieved. " +
                    "Email list operation must be invoked first to retrieve emails.");
        }
    }

    private void setProperties(MessageContext messageContext, EmailMessage emailMessage) {

        messageContext.setProperty(EmailPropertyNames.PROPERTY_HTML_CONTENT, emailMessage.getHtmlContent());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_TEXT_CONTENT, emailMessage.getTextContent());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_ID, emailMessage.getEmailId());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_TO, emailMessage.getTo());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_FROM, emailMessage.getFrom());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_CC, emailMessage.getCc());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_BCC, emailMessage.getBcc());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_SUBJECT, emailMessage.getSubject());
        messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAIL_REPLY_TO, emailMessage.getReplyTo());
    }
}
