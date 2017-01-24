/**
 * Personium
 * Copyright 2014 - 2017 FUJITSU LIMITED
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
package io.personium.client.http;

import java.util.HashMap;
import java.util.Map;

import io.personium.client.DaoException;

///**
// * RESTアクセスのためのクラス.
// */
/**
 * This is the interface for REST access.
 */
public interface IRestAdapter {
    // /** Content-Type に指定する文字列定義. */
    /** Content-Type for JSON. */
    String CONTENT_TYPE_JSON = "application/json";
    // /** Content-Type に指定する文字列定義. */
    /** Content-Type for XML. */
    String CONTENT_TYPE_XML = "application/xml";
    /** Content-Type application/ZIP. */
    String CONTENT_TYPE_ZIP = "application/zip";
    // /** Content-Type に指定する文字列定義. */
    /** Content-Type for FORMURLENCODE. */
    String CONTENT_FORMURLENCODE = "application/x-www-form-urlencoded";
    // /** ポストデータのエンコード種別. */
    /** Encoding type of post data. */
    String ENCODE = "UTF-8";

    // /** ステータスコード 300. */
    /** Status code 300. */
    int STATUS300 = 300;

    // /** MKCol用リクエストボディ. */
    /** MKCol for the request body. */
    String REQUEST_BODY_MKCOL_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<D:mkcol xmlns:D=\"DAV:\" xmlns:personium=\"urn:x-personium:xmlns\"><D:set><D:prop><D:resourcetype><D:collection/>"
            + "</D:resourcetype></D:prop></D:set></D:mkcol>";

    // /** MKOData用リクエストボディ. */
    /** MKOData for the request body. */
    String REQUEST_BODYMKODATA_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<D:mkcol xmlns:D=\"DAV:\" xmlns:personium=\"urn:x-personium:xmlns\"><D:set><D:prop><D:resourcetype><D:collection/>"
            + "<personium:odata/></D:resourcetype></D:prop></D:set></D:mkcol>";

    // /** サービスコレクション用リクエストボディ. */
    /** Collection services for the request body. */
    String REQUEST_BODY_SERVICE_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<D:mkcol xmlns:D=\"DAV:\" xmlns:personium=\"urn:x-personium:xmlns\"><D:set><D:prop><D:resourcetype>"
            + "<D:collection/><personium:service/></D:resourcetype></D:prop></D:set></D:mkcol>";

    // /** PROPPATCH用リクエストボディ. */
    /** PROPPATCH for the request body. */
    String REQUEST_BODY_PROPPATCH_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
            + "<D:propertyupdate xmlns:D=\"DAV:\" xmlns:personium=\"urn:x-personium:xmlns\" "
            + "xmlns:Z=\"http://www.w3.com/standards/z39.50/\"><D:set><D:prop>"
            + "<personium:service language=\"JavaScript\" subject=\"SERVICE_SUBJECT\">"
            + "<personium:path name=\"SERVICE_KEY\" src=\"SERVICE_VALUE\"/>"
            + "</personium:service></D:prop></D:set></D:propertyupdate>";

    // /**
    // * HttpClientを置き換える(ユニットテスト用).
    // * @param value HttpClientオブジェクト
    // */
    // void setHttpClient(HttpClient value);

    // /**
    // * レスポンスボディを受け取るGETメソッド.
    // * @param url リクエスト対象URL
    // * @param accept Acceptヘッダ値
    // * @return DcResponse型
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body.
     * @param url target Request URL
     * @param accept Accept Header Value
     * @return DcResponse type
     * @throws DaoException Exception thrown
     */
    PersoniumResponse get(String url, String accept) throws DaoException;

    // /**
    // * レスポンスボディを受け取るGETメソッド(If-None-Macth指定).
    // * @param url リクエスト対象URL
    // * @param accept Acceptヘッダ値
    // * @param etag 取得するEtag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body (If-None-Macth specified).
     * @param url target Request URL
     * @param accept Accept Header Value
     * @param etag Etag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse get(String url, String accept, String etag) throws DaoException;

    // /**
    // * レスポンスボディを受け取るGETメソッド(If-None-Macth指定).
    // * @param url リクエスト対象URL
    // * @param header リクエストヘッダ
    // * @param etag 取得するEtag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body using header (If-None-Macth specified).
     * @param url target Request URL
     * @param header Request Header
     * @param etag Etag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse get(String url, Map<String, String> header, String etag) throws DaoException;

    // /**
    // * HEADメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method gets the head for the specified URL.
     * @param url target Request URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse head(String url) throws DaoException;

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param map リクエストヘッダーのハッシュマップ
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body.
     * @param url target Request URL
     * @param map Request Header Map
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse put(String url, HashMap<String, String> map) throws DaoException;

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body and uses Etag value.
     * @param url target Request URL
     * @param data Data to be written
     * @param etag ETag value
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse put(String url, String data, String etag, String contentType) throws DaoException;

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param map リクエストヘッダーのハッシュマップ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body and uses Etag value and header map.
     * @param url target Request URL
     * @param data Data to be written
     * @param etag ETag value
     * @param map Hash Map of Request Header
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse put(String url, String data, String etag, HashMap<String, String> map, String contentType)
            throws DaoException;

    // /**
    // * レスポンスボディを受けるMERGEメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MERGE method that receives the response body.
     * @param url target Request URL
     * @param data Data to be written
     * @param etag ETag value
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse merge(String url, String data, String etag, String contentType) throws DaoException;

    // /**
    // * リクエストボディを受け取る POSTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the POST method that receives the request body.
     * @param url target Request URL
     * @param data Data to be written
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse post(String url, String data, String contentType) throws DaoException;

    // /**
    // * リクエストヘッダを指定するPOSTメソッド.
    // * @param url リクエスト対象URL
    // * @param map リクエストヘッダーのハッシュマップ
    // * @param data 書き込むデータ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the POST method that receives the request body and used header map.
     * @param url target Request URL
     * @param map HashMap of Request Header
     * @param data Data to be written
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse post(String url, HashMap<String, String> map, String data, String contentType) throws DaoException;

    // /**
    // * DELETEメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the DELETE method.
     * @param url target Request URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse del(String url) throws DaoException;

    // /**
    // * DELETEメソッド.
    // * @param url リクエスト対象URL
    // * @param etag DELETE対象のETag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the DELETE method and uses Etag value.
     * @param url target Request URL
     * @param etag ETag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    PersoniumResponse del(String url, String etag) throws DaoException;

}
