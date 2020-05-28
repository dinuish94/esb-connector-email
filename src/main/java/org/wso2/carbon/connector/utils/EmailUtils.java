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
import org.apache.commons.mail.util.MimeMessageParser;
import org.wso2.carbon.connector.connection.MailBoxConnection;
import org.wso2.carbon.connector.exception.EmailConnectionException;
import org.wso2.carbon.connector.exception.EmailParsingException;
import org.wso2.carbon.connector.pojo.Attachment;
import org.wso2.carbon.connector.pojo.EmailMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataSource;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;

import static java.lang.String.format;

public class EmailUtils {

    private EmailUtils() {

    }

    public static boolean changeEmailState(MailBoxConnection connection, String folder, String emailID, Flags flags,
                                        boolean expunge) throws EmailConnectionException {
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
                }
            }
            connection.closeFolder(expunge);
        } catch (MessagingException e) {
            throw new EmailConnectionException(format("Error occurred while changing email state. %s ",
                    e.getMessage()), e);
        }
        return success;
    }

    public static List<EmailMessage> getMessagesList(List<Message> messages) throws EmailParsingException {

        List<EmailMessage> messagesList = new ArrayList<>();
        for (Message message : messages) {
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) message);
            try {
                parser.parse();
                EmailMessage emailMessage = new EmailMessage();
                emailMessage.setTextContent(parser.getPlainContent());
                emailMessage.setHtmlContent(parser.getHtmlContent());
                emailMessage.setAttachments(getAttachmentList(parser.getAttachmentList()));
                emailMessage.setEmailId(parser.getMimeMessage().getMessageID());
                messagesList.add(emailMessage);
            } catch (Exception e) {
                throw new EmailParsingException(format("Error occurred while retrieving message data. %s ",
                        e.getMessage()), e);
            }
        }
        return messagesList;
    }

    private static List<Attachment> getAttachmentList(List<DataSource> dataSources) throws IOException {

        List<Attachment> attachments = new ArrayList<>();
        for (DataSource dataSource : dataSources) {
            Attachment attachment = new Attachment();
            attachment.setName(dataSource.getName());
            attachment.setContentType(dataSource.getContentType());
            attachment.setContent(dataSource.getInputStream());
            attachments.add(attachment);
        }
        return attachments;
    }

}
