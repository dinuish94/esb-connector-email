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

import java.io.IOException;
import java.util.Map;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MessageBuilder {

    private static Log log = LogFactory.getLog(MessageBuilder.class);

    private final MimeMessage message;

    private String attachments;
    private String contentType;
    private String contentTransferEncoding;
    private String content;

    private MessageBuilder(Session s) {

        this.message = new MimeMessage(s);
    }

    /**
     * Creates a new {@link MessageBuilder} instance for the specified {@code session}.
     *
     * @param session the {@link Session} for which the message is going to be created
     * @return a new {@link MessageBuilder} instance.
     */
    public static MessageBuilder newMessage(Session session) {

        return new MessageBuilder(session);
    }

    /**
     * Adds the subject to the {@link Message} that is being built.
     *
     * @param subject the subject of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set subject
     */
    public MessageBuilder withSubject(String subject) throws MessagingException {

        if (!StringUtils.isEmpty(subject)) {
            this.message.setSubject(subject);
        }
        return this;
    }

    public MessageBuilder replyTo(String replyTo) throws MessagingException {

        if (!StringUtils.isEmpty(replyTo)) {
            message.setReplyTo(InternetAddress.parse(replyTo));
        }
        return this;
    }

    /**
     * Adds the from addresses to the {@link Message} that is being built.
     *
     * @param fromAddresses the from addresses of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set 'from' address
     */
    public MessageBuilder fromAddresses(String fromAddresses) throws MessagingException {

        this.message.addFrom(InternetAddress.parse(fromAddresses));
        // TODO: Check address count and call single address method
        return this;
    }

    /**
     * Adds a single from address to the {@link Message} that is being built.
     *
     * @param from the from address of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set single address
     */
    public MessageBuilder fromSingleAddresses(String from) throws MessagingException {

        if (from != null) {
            this.message.setFrom(new InternetAddress(from));
        } else {
            this.message.setFrom();
        }
        return this;
    }

    /**
     * Adds the "to" (primary) addresses to the {@link Message} that is being built.
     *
     * @param toAddresses the primary addresses of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set 'to' recipient
     */
    public MessageBuilder to(String toAddresses) throws MessagingException {

        this.setRecipient(toAddresses, this.message, Message.RecipientType.TO);
        return this;
    }

    /**
     * Adds the "cc" addresses to the {@link Message} that is being built.
     *
     * @param ccAddresses the carbon copy addresses of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set 'cc' recipients
     */
    public MessageBuilder cc(String ccAddresses) throws MessagingException {

        this.setRecipient(ccAddresses, this.message, Message.RecipientType.CC);
        return this;
    }

    /**
     * Adds the "bcc" addresses to the {@link Message} that is being built.
     *
     * @param bccAddresses the blind carbon copy addresses of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set 'bcc' recipients
     */
    public MessageBuilder bcc(String bccAddresses) throws MessagingException {

        this.setRecipient(bccAddresses, this.message, Message.RecipientType.BCC);
        return this;
    }

    /**
     * Adds custom headers to the {@link Message} that is being built.
     *
     * @param headers the custom headers of the email.
     * @return this {@link MessageBuilder}
     * @throws MessagingException if failed to set headers
     */
    public MessageBuilder withHeaders(Map<String, String> headers) throws MessagingException {

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                this.message.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    private void setRecipient(String recipient, Message message, Message.RecipientType recipientType) throws MessagingException {

        if (!StringUtils.isEmpty(recipient)) {
            message.setRecipients(
                    recipientType,
                    InternetAddress.parse(recipient)
            );
        }
    }

    public MessageBuilder withBody(String content, String contentType, String encoding, String contentTransferEncoding) {

        this.contentType = StringUtils.isEmpty(contentType) ? EmailConstants.DEFAULT_CONTENT_TYPE : contentType;
        if (!StringUtils.isEmpty(encoding)) {
            this.contentType = contentType + "; charset=" + encoding;
        }
        if (!StringUtils.isEmpty(encoding)) {
            this.content = content;
        }
        if (!StringUtils.isEmpty(contentTransferEncoding)) {
            this.contentTransferEncoding = contentTransferEncoding;
        }
        return this;
    }

    public MessageBuilder withAttachments(String attachments) {

        if (!StringUtils.isEmpty(attachments)) {
            this.attachments = attachments;
        }
        return this;
    }

    /**
     * Build MIME Message using configured parameters
     *
     * @return MIME Message with the configured values
     * @throws MessagingException if failed to build message
     */
    public MimeMessage build() throws MessagingException {

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, contentType);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        if (attachments != null) {
            addAttachments(attachments, multipart);
        }
        message.setContent(multipart);
        if (contentTransferEncoding != null) {
            message.setHeader(EmailConstants.CONTENT_TRANSFER_ENCODING_HEADER, contentTransferEncoding);
        }
        return message;
    }

    /**
     * Add attachments to the message
     *
     * @param attachments Attachments to be added
     * @param multipart   Multipart Object the attachment should be added to
     * @throws MessagingException if failed to add the attachments
     */
    private void addAttachments(String attachments, Multipart multipart) throws MessagingException {

        String[] attachFiles = attachments.split(",");
        for (String filePath : attachFiles) {
            MimeBodyPart attachPart = new MimeBodyPart();
            try {
                attachPart.attachFile(filePath);
            } catch (IOException e) {
                log.error("Error occurred while attaching files. " + e.getMessage(), e);
            }
            multipart.addBodyPart(attachPart);
        }
    }
}
