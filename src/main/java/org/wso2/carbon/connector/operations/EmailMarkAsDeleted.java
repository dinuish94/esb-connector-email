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
package org.wso2.carbon.connector.operations;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.connection.EmailConnectionManager;
import org.wso2.carbon.connector.connection.EmailConnectionPool;
import org.wso2.carbon.connector.connection.MailBoxConnection;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.exception.EmailConnectionException;
import org.wso2.carbon.connector.exception.EmailConnectionPoolException;
import org.wso2.carbon.connector.utils.ConfigurationUtils;
import org.wso2.carbon.connector.utils.EmailConstants;
import org.wso2.carbon.connector.utils.EmailUtils;
import org.wso2.carbon.connector.utils.ResponseGenerator;

import javax.mail.Flags;
import javax.xml.stream.XMLStreamException;

import static java.lang.String.format;

public class EmailMarkAsDeleted extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        String folder = (String) getParameter(messageContext, EmailConstants.FOLDER);
        String emailID = (String) getParameter(messageContext, EmailConstants.EMAIL_ID);
        String connectionName = ConfigurationUtils.getConnectionName(messageContext);
        EmailConnectionPool pool = null;
        MailBoxConnection connection = null;
        try {
            pool = EmailConnectionManager.getEmailConnectionManager().getConnectionPool(connectionName);
            connection = (MailBoxConnection) pool.borrowObject();
            boolean status = EmailUtils.changeEmailState(connection, folder, emailID, new Flags(Flags.Flag.DELETED),
                    false);
            ResponseGenerator.generateOutput(messageContext, status);
        } catch (EmailConnectionException | EmailConnectionPoolException | XMLStreamException e) {
            handleException(format("Error occurred while marking email with ID: %s as deleted. %s", emailID,
                    e.getMessage()), e, messageContext);
        } finally {
            if (pool != null) {
                pool.returnObject(connection);
            }
        }

    }
}
