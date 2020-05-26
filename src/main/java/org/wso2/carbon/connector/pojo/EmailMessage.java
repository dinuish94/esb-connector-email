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
package org.wso2.carbon.connector.pojo;

import java.util.List;

public class EmailMessage {

    private String emailId;
    private String htmlContent;
    private String textContent;
    private List<Attachment> attachments;

    public String getHtmlContent() {

        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {

        this.htmlContent = htmlContent;
    }

    public String getTextContent() {

        return textContent;
    }

    public void setTextContent(String textContent) {

        this.textContent = textContent;
    }

    public List<Attachment> getAttachments() {

        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {

        this.attachments = attachments;
    }

    public String getEmailId() {

        return emailId;
    }

    public void setEmailId(String emailId) {

        this.emailId = emailId;
    }

    @Override
    public String toString() {

        return "EmailMessage{" +
                "htmlContent='" + htmlContent + '\'' +
                ", textContent='" + textContent + '\'' +
                ", attachments=" + attachments +
                '}';
    }
}
