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
package org.wso2.carbon.connector.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.connector.connection.MailBoxConnection;
import org.wso2.carbon.connector.exception.EmailConnectionException;
import org.wso2.carbon.connector.exception.EmailNotFoundException;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;

import static java.lang.String.format;

/**
 * Utilities for manipulating emails
 */
public final class EmailOperationUtils {

    private EmailOperationUtils() {

    }

    private static Log log = LogFactory.getLog(EmailOperationUtils.class);

    /**
     * Changes the email state by setting flags
     *
     * @param connection Mailbox connection to be used to connect to server
     * @param folder     Mailbox name
     * @param emailID    Email ID of the message of which the state is to be changed
     * @param flags      Flags to be set
     * @param expunge    whether to delete messages marked for deletion
     * @return true if the status update was successful, false otherwise
     * @throws EmailConnectionException thrown if failed to set the flags on the message
     */
    public static boolean changeEmailState(MailBoxConnection connection, String folder, String emailID, Flags flags,
                                           boolean expunge) throws EmailConnectionException, EmailNotFoundException {

        boolean success = false;
        if (StringUtils.isEmpty(folder)) {
            folder = EmailConstants.DEFAULT_FOLDER;
        }

        try {
            Folder inbox = connection.getFolder(folder, Folder.READ_WRITE);
            SearchTerm searchTerm = new MessageIDTerm(emailID);
            Message[] messages = inbox.search(searchTerm);

            if (messages.length > 0) {
                Message message = messages[0];
                if (flags != null) {
                    inbox.setFlags(new Message[]{message}, flags, true);
                    success = true;
                    if (log.isDebugEnabled()) {
                        log.debug(format("State updated for message with ID: %s...", emailID));
                    }
                }
            } else {
                log.error(format("No emails found with ID: %s.", emailID));
                throw new EmailNotFoundException(format("No emails found with ID: %s.", emailID));
            }
            connection.closeFolder(expunge);
        } catch (MessagingException e) {
            throw new EmailConnectionException(format("Error occurred while changing email state. %s ",
                    e.getMessage()), e);
        }
        return success;
    }
}
