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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.io.IOUtils;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.transport.passthru.util.RelayConstants;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;

public class ContentBuilder {

    public static String buildContent(InputStream inputStream, String contentType) {

        OMElement element = null;
        String content = null;
        SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
        SOAPEnvelope env = factory.getDefaultEnvelope();
        try {
            if (contentType.equalsIgnoreCase("text/plain")
                    || contentType.equalsIgnoreCase("text/csv")) {
                content = IOUtils.toString(inputStream);
                content = "<text>" + content + "</text>";
//                element = AXIOMUtil.stringToOM(content);
            } else if (contentType.equalsIgnoreCase("application/json")) {
                element = JsonUtil.toXml(inputStream, false);
            } else if (contentType.equalsIgnoreCase("application/xml")
                    || contentType.equalsIgnoreCase("text/xml")) {
//                element = AXIOMUtil.stringToOM(IOUtils.toString(inputStream));
                content = IOUtils.toString(inputStream);
            } else {
                OMNamespace ns = factory.createOMNamespace(
                        RelayConstants.BINARY_CONTENT_QNAME.getNamespaceURI(), "ns");
                element = factory.createOMElement(
                        RelayConstants.BINARY_CONTENT_QNAME.getLocalPart(), ns);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
//        env.getBody().addChild(element);
        return content;
    }

}
