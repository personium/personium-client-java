/**
 * Personium
 * Copyright 2014 - 2018 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.personium.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.personium.client.http.PersoniumRequestBuilder;
import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;
import io.personium.client.utils.UrlUtils;

///**
// * ServiceのCURDのためのクラス.
// */
/**
 * It creates a new object of ServiceCollection. This class performs CRUD operations for ServiceCollection.
 */
public class ServiceCollection extends PersoniumCollection {

    /**
     * Interface for configuration of service collection
     */
    public interface IPersoniumServiceRoute {
        /**
         * getter returning name of serivce collection
         */
        String getName();

        /**
         * getter returning source of service collection
         * @return
         */
        String getSrc();
    }

    /**
     * Internal class for configuration of service collection
     */
    class PersoniumServiceRoute implements IPersoniumServiceRoute {
        /** name of service route */
        private String name;

        /** source of service route */
        private String src;

        /**
         * This method returns name of service route
         */
        public String getName() {
            return this.name;
        }

        /**
         * This method returns source of service route
         */
        public String getSrc() {
            return this.src;
        }

        /**
         * This is the constructor
         * @param name name of service route
         * @param src source of service route
         */
        public PersoniumServiceRoute(String name, String src) {
            this.name = name;
            this.src = src;
        }
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public ServiceCollection(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param path パス文字列
    // */
    /**
     * This is the parameterized constructor with two arguments calling its parent constructor internally.
     * @param as Accessor
     * @param path string
     */
    public ServiceCollection(Accessor as, String path) {
        super(as, path);
    }

    /**
     * This method generate XML string for configuration of service.
     * @param routes list of route
     * @param subject Value of the service subject
     * @return String XML for configuration
     */
    public String createConfigureXML(List<IPersoniumServiceRoute> routes, String subject) {
        StringWriter sw = new StringWriter();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation domImpl = builder.getDOMImplementation();

            String nsD = "DAV:";
            String nsP = "urn:x-personium:xmlns";

            Document document = domImpl.createDocument(nsD, "D:propertyupdate", null);
            Element elemSet = document.createElementNS(nsD, "D:set");
            Element elemProp = document.createElementNS(nsD, "D:prop");
            document.getDocumentElement().appendChild(elemSet);
            elemSet.appendChild(elemProp);

            Element elemService = document.createElementNS(nsP, "p:service");
            elemService.setAttribute("language", "JavaScript");
            elemService.setAttribute("subject", subject);
            elemProp.appendChild(elemService);

            for (IPersoniumServiceRoute route : routes) {
                Element elemPath = document.createElementNS(nsP, "p:path");
                elemPath.setAttribute("name", route.getName());
                elemPath.setAttribute("src", route.getSrc());
                elemService.appendChild(elemPath);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new DOMSource(document), new StreamResult(sw));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    // /**
    // * サービスの設定.
    // * @param key プロパティ名
    // * @param value プロパティの値
    // * @param subject サービスサブジェクトの値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method configures a set of services.
     * @param key name of service route
     * @param value source of service route
     * @param subject Value of the service subject
     * @throws DaoException Exception thrown
     */
    public void configure(String key, String value, String subject) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        ArrayList<IPersoniumServiceRoute> routes = new ArrayList<>();
        routes.add(new PersoniumServiceRoute(key, value));
        configure(routes, subject);
    }

    /**
     * This method configures a set of services.
     * @param routes list of route
     * @param subject Value of the service subject
     * @throws DaoException Exception thrown
     */
    public void configure(List<IPersoniumServiceRoute> routes, String subject) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        String configureXML = createConfigureXML(routes, subject);
        rest.proppatch(this.getPath(), configureXML);
    }

    // /**
    // * Call the Engine Service.
    // * @param method HTTP Request Method
    // * @param name 実行するサービス名
    // * @param body HTTP Request Body
    // * @return PersoniumResponseオブジェクト
    // */
    /**
     * This method is used to call the Engine Service. It internally calls its overloaded version.
     * @param method HTTP Request Method
     * @param name Service name to be executed
     * @param body HTTP Request Body
     * @param contentType CONTENT-TYPE value
     * @return PersoniumResponse object
     */
    public PersoniumResponse call(String method, String name, String body, String contentType) {
        return this.call(method, name, body, null, contentType);
    }

    // /**
    // * Call the Engine Service with extra argument header map.
    // * @param method Http Method
    // * @param name 実行するサービス名
    // * @param body リクエストボディ
    // * @param headers header map key value pair
    // * @return PersoniumResponseオブジェクト
    // */
    /**
     * This method is used to call the Engine Service with extra argument header map.
     * @param method Http Method
     * @param name Service name to be executed
     * @param body HTTP Request Body
     * @param headers header map key value pair
     * @param contentType CONTENT-TYPE value
     * @return PersoniumResponse object
     */
    public PersoniumResponse call(
            String method, String name, String body, Map<String, String> headers, String contentType) {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        String url = UrlUtils.append(this.getPath(), name);
        PersoniumRequestBuilder drb = new PersoniumRequestBuilder().url(url).method(method).contentType(contentType)
                .token(this.accessor.getAccessToken());

        /** add the headers to request builder */
        if (headers != null && headers.size() > 0) {
            Set<Entry<String, String>> entrySet = headers.entrySet();
            for (Entry<String, String> entry : entrySet) {
                drb.header(entry.getKey(), entry.getValue());
            }
        }

        if (body != null && !"".equals(body)) {
            drb.body(body);
        }
        HttpResponse response = null;
        try {
            HttpUriRequest req = drb.build();
            response = rest.getHttpClient().execute(req);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new PersoniumResponse(response);
    }

    // /**
    // * 指定Pathに任意の文字列データをPUTします.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param data PUTするデータ
    // * @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to PUT a string of data to any specified Path.
     * @param pathValue DAV Path
     * @param contentType Media type
     * @param data PUT data
     * @param etagValue ETag of PUT target. Specify "*" for forcing new or updated
     * @throws DaoException Exception thrown
     */
    public void put(String pathValue, String contentType, String data, String etagValue) throws DaoException {
        byte[] bs;
        try {
            bs = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DaoException("UnsupportedEncodingException", e);
        }
        InputStream is = new ByteArrayInputStream(bs);
        this.put(pathValue, contentType, is, etagValue);
    }

    // /**
    // * 指定pathに任意のInputStreamの内容をPUTします. 指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param is InputStream
    // * @param etagValue ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used To PUT the contents of the InputStream of any specified path. Rewrites it, if the specified
     * object ID already exists, or creates a new one if it does not exist.
     * @param pathValue DAV Path
     * @param contentType Media type
     * @param is InputStream
     * @param etagValue ETag value
     * @throws DaoException Exception thrown
     */
    public void put(String pathValue, String contentType, InputStream is, String etagValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), "__src/" + pathValue);
        ((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url, contentType, is, etagValue);
    }

    // /**
    // * 指定PathのデータをDeleteします.
    // * @param pathValue DAVのパス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the data in the path specified.
     * @param pathValue DAV Path
     * @throws DaoException Exception thrown
     */
    public void del(String pathValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), "__src/" + pathValue);
        RestAdapterFactory.create(this.accessor).del(url, "*");
    }
}
