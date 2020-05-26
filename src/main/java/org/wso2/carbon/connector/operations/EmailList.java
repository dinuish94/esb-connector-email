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

import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.connection.EmailConnectionManager;
import org.wso2.carbon.connector.connection.EmailConnectionPool;
import org.wso2.carbon.connector.connection.MailBoxConnection;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.exception.EmailConnectionException;
import org.wso2.carbon.connector.exception.EmailConnectionPoolException;
import org.wso2.carbon.connector.exception.EmailParsingException;
import org.wso2.carbon.connector.pojo.EmailMessage;
import org.wso2.carbon.connector.pojo.MailboxConfiguration;
import org.wso2.carbon.connector.utils.ConfigurationUtils;
import org.wso2.carbon.connector.utils.EmailPropertyNames;
import org.wso2.carbon.connector.utils.EmailUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import static java.lang.String.format;
import static java.util.Date.from;

public class EmailList extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) {

        String connectionName = ConfigurationUtils.getConnectionName(messageContext);
        EmailConnectionPool pool = null;
        MailBoxConnection connection = null;
        try {
            pool = EmailConnectionManager.getEmailConnectionManager().getConnectionPool(connectionName);
            connection = (MailBoxConnection) pool.borrowObject();
            retrieveMessages(connection, messageContext);
        } catch (EmailConnectionException | EmailConnectionPoolException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), e, messageContext);
        } finally {
            if (pool != null) {
                pool.returnObject(connection);
            }
        }
    }

    private void retrieveMessages(MailBoxConnection connection, MessageContext messageContext) {

        MailboxConfiguration mailboxConfiguration = ConfigurationUtils.getMailboxConfigFromContext(messageContext);
        String folderName = mailboxConfiguration.getFolder();

        try {
            Folder mailbox;

            if (mailboxConfiguration.getDeleteAfterRetrieve()) {
                mailbox = connection.getFolder(folderName, Folder.READ_WRITE);
            } else {
                mailbox = connection.getFolder(folderName, Folder.READ_ONLY);
            }

            Message[] messages = mailbox.search(getSearchTerm(mailboxConfiguration));

            List<EmailMessage> messageList = EmailUtils.getMessagesList(getPaginatedMessages(messages,
                    mailboxConfiguration.getOffset(),
                    mailboxConfiguration.getLimit()));

            messageContext.setProperty(EmailPropertyNames.PROPERTY_EMAILS, messageList);

            connection.closeFolder(false);
        } catch (EmailParsingException | MessagingException e) {
            handleException(e.getMessage(), e, messageContext);
            log.error(format("Error occurred while retrieving messages. %s", e.getMessage()), e);
        }
    }

    private List<Message> getPaginatedMessages(Message[] messages, int offset, int limit) {

        List<Message> messageList = Arrays.asList(messages);
        int toIndex = offset + limit;
        if (toIndex > messageList.size()) {
            toIndex = messageList.size();
        }
        if (messageList.size() >= offset) {
            messageList = messageList.subList(offset, toIndex);
        }
        return messageList;
    }

    private SearchTerm getSearchTerm(MailboxConfiguration mailboxConfiguration) throws AddressException {

        FlagTerm seenFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), mailboxConfiguration.getSeen());
        FlagTerm answeredFlagTerm = new FlagTerm(new Flags(Flags.Flag.ANSWERED), mailboxConfiguration.getAnswered());
        FlagTerm recentFlagTerm = new FlagTerm(new Flags(Flags.Flag.RECENT), mailboxConfiguration.getRecent());
        FlagTerm deletedFlagTerm = new FlagTerm(new Flags(Flags.Flag.DELETED), mailboxConfiguration.getDeleted());

        AndTerm searchTerm = new AndTerm(deletedFlagTerm, new AndTerm(seenFlagTerm, new AndTerm(answeredFlagTerm, recentFlagTerm)));

        String subjectRegex = mailboxConfiguration.getSubjectRegex();
        if (!StringUtils.isEmpty(subjectRegex)) {
            SubjectTerm subjectTerm = new SubjectTerm(subjectRegex);
            searchTerm = new AndTerm(searchTerm, subjectTerm);
        }

        String fromRegex = mailboxConfiguration.getFromRegex();
        if (!StringUtils.isEmpty(fromRegex)) {
            FromTerm fromTerm = new FromTerm(new InternetAddress(fromRegex));
            searchTerm = new AndTerm(searchTerm, fromTerm);
        }

        String receivedSince = mailboxConfiguration.getReceivedSince();
        if (receivedSince != null) {
            LocalDateTime date = LocalDateTime.parse(receivedSince);
            ReceivedDateTerm receivedSinceDateTerm = new ReceivedDateTerm(ComparisonTerm.GT, from(date.atZone(ZoneId.systemDefault()).toInstant()));
            searchTerm = new AndTerm(searchTerm, receivedSinceDateTerm);
        }

        String receivedUntil = mailboxConfiguration.getReceivedUntil();
        if (receivedUntil != null) {
            LocalDateTime date = LocalDateTime.parse(receivedUntil);
            ReceivedDateTerm receivedUntilDateTerm = new ReceivedDateTerm(ComparisonTerm.LT, from(date.atZone(ZoneId.systemDefault()).toInstant()));
            searchTerm = new AndTerm(searchTerm, receivedUntilDateTerm);
        }

        String sentSince = mailboxConfiguration.getSentSince();
        if (sentSince != null) {
            LocalDateTime date = LocalDateTime.parse(sentSince);
            ReceivedDateTerm sentSinceDateTerm = new ReceivedDateTerm(ComparisonTerm.GT, from(date.atZone(ZoneId.systemDefault()).toInstant()));
            searchTerm = new AndTerm(searchTerm, sentSinceDateTerm);
        }

        String sentUntil = mailboxConfiguration.getSentUntil();
        if (sentUntil != null) {
            LocalDateTime date = LocalDateTime.parse(sentUntil);
            ReceivedDateTerm sentUntilDateTerm = new ReceivedDateTerm(ComparisonTerm.LT, from(date.atZone(ZoneId.systemDefault()).toInstant()));
            searchTerm = new AndTerm(searchTerm, sentUntilDateTerm);
        }
        return searchTerm;
    }
}
