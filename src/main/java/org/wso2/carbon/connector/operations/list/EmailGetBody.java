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

public class EmailGetBody extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext){
        String emailIndex = (String) getParameter(messageContext, EmailConstants.EMAIL_INDEX);
        List<EmailMessage> emailMessages = (List<EmailMessage>) messageContext.getProperty(EmailPropertyNames.PROPERTY_EMAILS);

        if (emailIndex != null && emailMessages != null) {
            EmailMessage emailMessage = emailMessages.get(Integer.parseInt(emailIndex));
            messageContext.setProperty("HTML_CONTENT", emailMessage.getHtmlContent());
            messageContext.setProperty("TEXT_CONTENT", emailMessage.getTextContent());
        }
    }
}
