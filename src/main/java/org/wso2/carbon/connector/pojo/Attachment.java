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

import java.io.InputStream;

public class Attachment {

    private String name;
    private String contentType;
    private InputStream content;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getContentType() {

        return contentType;
    }

    public void setContentType(String contentType) {

        this.contentType = contentType;
    }

    public InputStream getContent() {

        return content;
    }

    public void setContent(InputStream content) {

        this.content = content;
    }

    @Override
    public String toString() {

        return "Attachment{" +
                "name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
