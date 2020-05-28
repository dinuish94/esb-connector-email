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

import java.util.List;

public class EmailGetAttachment extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        String emailIndex = (String) getParameter(messageContext, EmailConstants.EMAIL_INDEX);
        String attachmentIndex = (String) getParameter(messageContext, EmailConstants.ATTACHMENT_INDEX);
        List<EmailMessage> emailMessages =  (List<EmailMessage>) messageContext.getProperty(EmailPropertyNames.PROPERTY_EMAILS);

        if (emailIndex != null && attachmentIndex != null && emailMessages != null) {
            EmailMessage emailMessage = emailMessages.get(Integer.parseInt(emailIndex));
            Attachment attachment = emailMessage.getAttachments().get(Integer.parseInt(attachmentIndex));
            messageContext.setProperty(EmailPropertyNames.PROPERTY_ATTACHMENT_TYPE, attachment.getContentType());
            messageContext.setProperty(EmailPropertyNames.PROPERTY_ATTACHMENT_NAME, attachment.getName());
            try {
                ContentBuilder.buildContent(messageContext, attachment.getContent(), attachment.getContentType());
            } catch (ContentBuilderException e) {
                handleException("Error occurred during setting attachment content.", e, messageContext);
            }
        }
    }
}
