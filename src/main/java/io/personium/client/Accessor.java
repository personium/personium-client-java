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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import io.personium.client.http.BatchAdapter;
import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;
import io.personium.client.utils.UrlUtils;

/**
 * It creates a new object of Accessor. This is the base class for setting the access parameters to access Cloud data.
 */
public class Accessor implements Cloneable {

    /** Final variable for holding key for TOKEN. */
    public static final String KEY_TOKEN = "token";

    /** Expiration date of the token. */
    private Number expiresIn = null;
    /** Access token. */
    private String accessToken = null;
    /** Expiration date of the refresh token. */
    private Number refreshExpiresIn = null;
    /** Refresh Token. */
    private String refreshToken = null;
    /** Token type. */
    private String tokenType = null;

    /** Parameter to represent level of schema authorization. */
    private JSONObject schemaAuth;

    /** Holds the type of "token", etc. */
    private String accessType = "";
    /** Authorised cell URL. */
    private String authCellUrl;
    /** Authentication User ID. */
    private String userId;
    /** Authentication password. */
    private String password;

    /** Schema. */
    private String schema;
    /** Schema authentication ID. */
    private String schemaUserId;
    /** Schema authentication password. */
    private String schemaPassword;

    /** Cell name. */
    protected String targetCellName;

    /** Transformer cell token. */
    private String transCellToken;
    /** Transformer cell refresh token. */
    private String transCellRefreshToken;

    /** Owner. */
    protected boolean owner = false;

    /** Current Box Schema. */
    private String boxSchema = "";
    /** Current Box Name. */
    private String boxName = "";
    /** Base URL. */
    private String baseUrl = "";

    /** Reference to PersoniumContext. */
    private PersoniumContext context;
    /** Cell. */
    private Cell currentCell;

    /** Batch Mode. */
    private boolean batch;

    /** BatchAdapter. */
    private BatchAdapter batchAdapter;

    /** Default header. */
    HashMap<String, String> defaultHeaders;

    /** Response header acquired from the server response. */
    private HashMap<String, String> resHeaders = new HashMap<String, String>();

    /**
     * This is the parameterized constructor initializing the various class variables.
     * @param personiumContext PersoniumContext
     */
    public Accessor(PersoniumContext personiumContext) {
        this.expiresIn = 0;
        this.refreshExpiresIn = 0;
        this.context = personiumContext;
        this.baseUrl = personiumContext.getBaseUrl();
        this.authCellUrl = personiumContext.getCellName();
        this.boxSchema = personiumContext.getBoxSchema();
        this.boxName = personiumContext.getBoxName();
    }

    /**
     * This method is used to initialize the class variable accessor.
     * @return Accessor object
     */
    public Accessor clone() {
        try {
            return (Accessor) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * This method returns the specified cell. It does not take any parameter.
     * @return Cell object
     * @throws DaoException Exception thrown
     */
    public Cell cell() throws DaoException {
        return this.cell(this.getCellName());
    }

    /**
     * This method returns the cell specified in the parameter.
     * @param cell Destination Cell URL
     * @return Cell instance
     * @throws DaoException Exception thrown
     */
    public Cell cell(String cell) throws DaoException {
        if (!this.authCellUrl.equals(cell)) {
            this.targetCellName = cell;
        }
        // Unit昇格時はこのタイミングで認証を行わない
        /** Authentication is not performed. */
        if (!this.owner) {
            certification();
        }
        return new Cell(this, cell);
    }

    /**
     * This method changes the current password.
     * @param newPassword Password new value
     * @throws DaoException Exception thrown
     */
    public void changePassword(String newPassword) throws DaoException {
        if (this.accessToken == null) {
            /** authentication is performed when accessToken is not present. */
            certification();
        }
        /** Password change. */
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("X-Personium-Credential", newPassword);
        /** Create the URL for password change. */
        String cellUrl = this.getCellName();
        if (!UrlUtils.isUrl(cellUrl)) {
            cellUrl = UrlUtils.append(this.getBaseUrl(), this.getCellName());
        }
        String url = UrlUtils.append(cellUrl, "__mypassword");

        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this);
        rest.put(url, headers);
        // password変更でエラーの場合は例外がthrowされるので例外で無い場合は、
        // Accessorのpasswordを新しいのに変えておく
        this.password = newPassword;
    }

    /**
     * This method returns $Batch - batch mode.
     * @return batch mode
     */
    public final boolean isBatchMode() {
        return batch;
    }

    /**
     * This method sets $Batch - batch mode.
     * @param batch mode
     */
    public final void setBatch(boolean batch) {
        this.batchAdapter = new BatchAdapter(this);
        this.batch = batch;
    }

    /**
     * This method generates BatchAdapter instance if not created before.
     * @return BatchAdapter object
     */
    public BatchAdapter getBatchAdapter() {
        if (null == this.batchAdapter) {
            this.batchAdapter = new BatchAdapter(this);
        }
        return this.batchAdapter;
    }

    /**
     * This method performs Unit Promotion by creating and initializing OwnerAccessor.
     * @return Promoted OwnerAccessor
     * @throws DaoException Exception thrown
     */
    public OwnerAccessor asCellOwner() throws DaoException {
        OwnerAccessor oas;
        oas = new OwnerAccessor(this.context, this);
        oas.defaultHeaders = this.defaultHeaders;
        return oas;
    }

    /**
     * This method returns the global access token.
     * @return global token
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * This method sets the default header.
     * @param value Default header Map
     */
    public void setDefaultHeaders(HashMap<String, String> value) {
        this.defaultHeaders = value;
    }

    /**
     * This method gets the default header.
     * @return Default header
     */
    public HashMap<String, String> getDefaultHeaders() {
        return this.defaultHeaders;
    }

    /**
     * This method sets the global access token.
     * @param token Access Token
     */
    protected void setAccessToken(String token) {
        this.accessToken = token;
    }

    /**
     * This method gets the DaoConfig object.
     * @return DaoConfig object
     */
    public DaoConfig getDaoConfig() {
        return context.getDaoConfig();
    }

    /**
     * This method returns the PersoniumContext object.
     * @return PersoniumContext
     */
    protected PersoniumContext getContext() {
        return this.context;
    }

    /**
     * This method sets the PersoniumContext object.
     * @param c PersoniumContext
     */
    void setContext(PersoniumContext c) {
        this.context = c;
    }

    /**
     * This method returns the current cell being accessed.
     * @return Current cell object
     */
    Cell getCurrentCell() {
        return this.currentCell;
    }

    /**
     * This method sets the current cell as specified.
     * @param cell Cell object
     */
    void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }

    /**
     * This method returns the expiration value of token.
     * @return Expiration date of the token
     */
    public Number getExpiresIn() {
        return this.expiresIn;
    }

    /**
     * This method sets the expiration value of token.
     * @param expiresIn Expiration date of the token
     */
    protected void setExpiresIn(Number expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * This method returns the refresh token value.
     * @return Refresh token value
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * This method sets the refresh token value.
     * @param token Refresh token value
     */
    protected void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    /**
     * This method returns the token type.
     * @return token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * This method sets the token type.
     * @param type token type
     */
    protected void setTokenType(String type) {
        this.tokenType = type;
    }

    /**
     * This method returns the expiration date of refresh token.
     * @return expiration date of refresh token
     */
    public Number getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    /**
     * This method sets the expiration date of refresh token.
     * @param refreshExpiresIn expiration date of refresh token
     */
    protected void setRefreshExpiresIn(Number refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    /**
     * This method returns the authorized cell URL.
     * @return CellName URL
     */
    String getCellName() {
        return this.authCellUrl;
    }

    /**
     * This method sets the CellName value.
     * @param name CellName value
     */
    void setCellName(String name) {
        this.authCellUrl = name;
    }

    /**
     * This method gets the Box Schema value.
     * @return BoxSchema value
     */
    protected String getBoxSchema() {
        return this.boxSchema;
    }

    /**
     * This method sets the Box Schema value.
     * @param uri Box Schema value
     */
    void setBoxSchema(String uri) {
        this.boxSchema = uri;
    }

    /**
     * This method returns theBox Name value.
     * @return Box Name
     */
    String getBoxName() {
        return this.boxName;
    }

    /**
     * This method sets the Box Name value.
     * @param value Box Name
     */
    void setBoxName(String value) {
        this.boxName = value;
    }

    /**
     * This method gets the base URL value.
     * @return BaseURL value
     */
    String getBaseUrl() {
        return this.baseUrl;
    }

    /**
     * This method sets the base URL value.
     * @param value BaseURL value
     */
    void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    /**
     * This method returns the userId.
     * @return the userId
     */
    String getUserId() {
        return userId;
    }

    /**
     * This method sets the userId.
     * @param userId the userId to set
     */
    void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * This method retruns the password.
     * @return the password
     */
    String getPassword() {
        return password;
    }

    /**
     * This method sets the password.
     * @param password the password to set
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method returns the schema.
     * @return the schema
     */
    String getSchema() {
        return schema;
    }

    /**
     * This method sets the schema.
     * @param schema the schema to set
     */
    void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * This method returns the target cell name.
     * @return the targetCellName
     */
    String getTargetCellName() {
        return targetCellName;
    }

    /**
     * This method sets the target cell name.
     * @param targetCellName the targetCellName to set
     */
    void setTargetCellName(String targetCellName) {
        this.targetCellName = targetCellName;
    }

    /**
     * This method sets the access type.
     * @param accessType the accessType to set
     */
    protected void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    /**
     * This method gets the access type "token", etc.
     * @return access type
     */
    protected String getAccessType() {
        return this.accessType;
    }

    /**
     * This method sets the transformer cell token.
     * @param transCellToken the trancCellToken to set
     */
    void setTransCellToken(String trancCellToken) {
        this.transCellToken = trancCellToken;
    }

    /**
     * This method gets the transformer cell token.
     * @param the transCellToken
     */
    String getTransCellToken() {
        return this.transCellToken;
    }

    /**
     * This method sets the transformer cell refresh token.
     * @param trancCellRefreshToken the trancCellRefreshToken to set
     */
    void setTransCellRefreshToken(String trancCellRefreshToken) {
        this.transCellRefreshToken = trancCellRefreshToken;
    }

    /**
     * This method gets the transformer cell refresh token.
     * @param the trancCellRefreshToken
     */
    String getTransCellRefreshToken() {
        return this.transCellRefreshToken;
    }

    /**
     * This method sets the schema user ID.
     * @param schemaUserId the schemaUserId to set
     */
    void setSchemaUserId(String schemaUserId) {
        this.schemaUserId = schemaUserId;
    }

    /**
     * This method gets the schema user ID.
     * @param the schemaUserId
     */
    String getSchemaUserId() {
        return this.schemaUserId;
    }

    /**
     * This method sets the schema password.
     * @param schemaPassword the schemaPassword to set
     */
    void setSchemaPassword(String schemaPassword) {
        this.schemaPassword = schemaPassword;
    }

    /**
     * This method gets the schema password.
     * @param the schemaPassword
     */
    String getSchemaPassword() {
        return this.schemaPassword;
    }

    /**
     * This method sets the response headers that are retrieved from the server response.
     * @param headers Response headers set
     */
    public void setResHeaders(Header[] headers) {
        for (Header header : headers) {
            this.resHeaders.put(header.getName(), header.getValue());
            if (header.getName().equals(PersoniumContext.PERSONIUM_VERSION)) {
                this.context.setServerVersion(header.getValue());
            }
        }
    }

    /**
     * This method gets the response headers retrieved from the server response.
     * @return Response Headers
     */
    Map<String, String> getResHeaders() {
        return this.resHeaders;
    }

    /**
     * This method performs authentication of user credentails based on token type etc.
     * @throws DaoException Exception thrown
     */
    protected void certification() throws DaoException {

        /** If the access type is "token", then authentication process is not performed. */
        if (this.accessType.equals(Accessor.KEY_TOKEN)) {
            return;
        }

        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this);
        /** Create a url to authenticate. */
        String authUrl = createCertificatUrl();

        /** Make a request body to authenticate. */
        StringBuilder requestBody = new StringBuilder();
        if (this.transCellToken != null) {
            /** Transformer cell token authentication. */
            requestBody.append("grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion=");
            requestBody.append(this.transCellToken);
        } else if (this.transCellRefreshToken != null) {
            /** Refresh token authentication. */
            requestBody.append("grant_type=refresh_token&refresh_token=");
            requestBody.append(this.transCellRefreshToken);
        } else if (userId != null) {
            /** Password authentication. */
            requestBody.append("grant_type=password&username=");
            requestBody.append(this.userId);
            requestBody.append("&password=");
            requestBody.append(this.password);
        }

        /** Create Target URL. */
        if (this.targetCellName != null) {
            requestBody.append("&p_target=");
            if (UrlUtils.isUrl(this.targetCellName)) {
                requestBody.append(this.targetCellName);
            } else {
                requestBody.append(UrlUtils.append(baseUrl, this.targetCellName));
            }
        }

        /** Add the schema information for authentication schema. */
        if (this.schemaUserId != null && this.schemaPassword != null) {
            /** Authentication schema. */
            StringBuilder schemaRequestBody = new StringBuilder();
            schemaRequestBody.append("grant_type=password&username=");
            schemaRequestBody.append(this.schemaUserId);
            schemaRequestBody.append("&password=");
            schemaRequestBody.append(this.schemaPassword);
            schemaRequestBody.append("&p_target=");
            schemaRequestBody.append(authUrl);
            /** If this is not the Url, add the schema name to BaseURL. */
            if (!UrlUtils.isUrl(this.schema)) {
                this.schema = UrlUtils.append(baseUrl, this.schema);
            }

            if (!this.schema.endsWith("/")) {
                this.schema += "/";
            }

            PersoniumResponse res = rest.post(UrlUtils.append(this.schema, "__token"), schemaRequestBody.toString(),
                    RestAdapter.CONTENT_FORMURLENCODE, false);
            this.schemaAuth = res.bodyAsJson();

            requestBody.append("&client_id=");
            requestBody.append(this.schema);
            requestBody.append("&client_secret=");
            requestBody.append((String) this.schemaAuth.get("access_token"));
        }
        if (owner) {
            requestBody.append("&cell_owner=true");
        }
        /** To hold the token to authenticate. */
        PersoniumResponse res = rest.post(UrlUtils.append(authUrl, "__token"), requestBody.toString(),
                RestAdapter.CONTENT_FORMURLENCODE, false);
        JSONObject json = res.bodyAsJson();
        this.accessToken = (String) json.get("access_token");
        this.expiresIn = (Number) json.get("expires_in");
        this.refreshToken = (String) json.get("refresh_token");
        this.refreshExpiresIn = (Number) json.get("refresh_token_expires_in");
        this.tokenType = (String) json.get("token_type");
    }

    /**
     * This method returns the transformer cell token as XML.
     * @return XML DOM object
     */
    private Document trancCellTokenAsXml() throws DaoException {
        String saml;
        try {
            saml = new String(Base64.decodeBase64(this.transCellToken), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw DaoException.create(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        InputStream is = new ByteArrayInputStream(saml.getBytes());
        try {
            document = builder.parse(is);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    /**
     * This method creates a url of authentication destination Cell.
     * @return authentication destination Cell URL
     * @throws DaoException Exception thrown
     */
    private String createCertificatUrl() throws DaoException {
        /** Create a url to authenticate. */
        String authUrl;
        if (this.transCellToken != null) {
            /** Get the connection destination url from the token. */
            Document doc = trancCellTokenAsXml();
            authUrl = doc.getElementsByTagName("Audience").item(0).getFirstChild().getNodeValue();
        } else if (UrlUtils.isUrl(this.authCellUrl)) {
            authUrl = this.authCellUrl;
        } else {
            authUrl = UrlUtils.append(baseUrl, this.authCellUrl);
        }
        return authUrl;
    }

    /**
     * This method gets the global token of the cell.
     * @return token Empty string
     */
    protected String loadGlobalToken() {
        return "";
    }

    /**
     * This method gets the local token for the box.
     * @return token Empty string
     */
    protected String loadClientToken() {
        return "";
    }

}
