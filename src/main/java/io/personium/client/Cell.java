/**
 * Personium
 * Copyright 2014-2021 Personium Project Authors
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

import java.io.InputStream;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import io.personium.client.http.IRestAdapter;
import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;
import io.personium.client.utils.UrlUtils;
import io.personium.client.utils.Utils;

///**
// * Cell へアクセスするためのクラス.
// */
/**
 * It creates a new object of Cell. This class represents Cell object to perform cell related operations.
 */
public class Cell extends AbstractODataContext {
    // /** キャメル方で表現したクラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "cell";

    // /** Cell名. */
    /** Cell Name. */
    private String name;
    /** Cell URL. */
    private String url;
    /** Location. */
    private String location;

    // CHECKSTYLE:OFF
    // /** CellレベルACLへアクセスするためのクラス. */
    /** Class to access to the Cell level ACL. */
    public AclManager acl; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** メンバーへアクセスするためのクラスインスタンス。cell().accountでアクセス. */
    /** Class instance to access the member AccountManager. */
    public AccountManager account; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** BoxのCRUDを行うマネージャクラス. */
    /** Manager class to perform CRUD of Box. */
    public BoxManager box; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** BoxのCRUDを行うマネージャクラス. */
    /** Manager class to perform CRUD of Box. */
    public BoxManager boxManager; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** Relation へアクセスするためのクラス. */
    /** Manager class to perform CRUD of Relation. */
    public RelationManager relation; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** Role へアクセスするためのクラス. */
    /** Manager class to perform CRUD of Role. */
    public RoleManager role; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** ExtRole へアクセスするためのクラス. */
    /** Manager class to perform CRUD of ExternalRole. */
    public ExtRoleManager extRole; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** ExtCell へアクセスするためのクラス. */
    /** Manager class to perform CRUD of ExternalCell. */
    public ExtCellManager extCell; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** cellレベルEventへアクセスするためのクラス. */
    /** Manager class to perform CRUD of Event. */
    public EventManager event; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** cellレベルEvent(current)へアクセスするためのクラス. */
    /** Class variable to access the (current) cell level Event. */
    public LogManager currentLog; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.
    // /** cellレベルEvent(archive)へアクセスするためのクラス. */
    /** Class variable to access the (archive) cell level Event. */
    public LogManager archiveLog; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.

    // CHECKSTYLE:ON

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor.
     */
    public Cell() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with one parameter and calling its another constructor.
     * @param as Accessor
     * @throws DaoException Exception thrown
     */
    public Cell(Accessor as) throws DaoException {
        this(as, as.getCellName());
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param key 対象Cell
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with two parameters using accessor and cell name and calling initialize
     * method.
     * @param as Accessor
     * @param key Cell Name
     * @throws DaoException Exception thrown
     */
    public Cell(Accessor as, String key) throws DaoException {
        super(as);
        this.name = key;
        this.initialize(as, null);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するCellのJson
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with two parameters and calling initialize method.
     * @param as Accessor
     * @param body JSON Body
     * @throws DaoException Exception thrown
     */
    public Cell(Accessor as, JSONObject body) throws DaoException {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから返却されたJSONオブジェクト
    // */
    /**
     * This method is used to initialize various class variables.
     * @param as Accessor
     * @param json JSON object
     * @throws DaoException DaoException
     */
    public void initialize(Accessor as, JSONObject json) throws DaoException {
        super.initialize(as);
        if (json != null) {
            this.rawData = json;
            this.name = (String) json.get("Name");
            this.location = (String) ((JSONObject) json.get("__metadata")).get("uri");
        }
        this.name = accessor.getContext().extractCellName(name);
        this.url = accessor.getContext().makeCellUrl(name);
        this.accessor.setCurrentCell(this);
        this.relation = new RelationManager(this.accessor);
        this.role = new RoleManager(this.accessor);
        this.acl = new AclManager(this.accessor, this.url);
        this.account = new AccountManager(this.accessor);
        this.box = new BoxManager(this.accessor);
        this.boxManager = new BoxManager(this.accessor);
        this.extRole = new ExtRoleManager(this.accessor);
        this.extCell = new ExtCellManager(this.accessor);
        this.event = new EventManager(this.accessor);
        this.currentLog = new CurrentLogManager(this.accessor);
        this.archiveLog = new ArchiveLogManager(this.accessor);
    }

    // /**
    // * Cell名を取得.
    // * @return Cell名
    // */
    /**
     * This method returns the Cell Name.
     * @return Cell Name value
     */
    public String getName() {
        return name;
    }

    // /**
    // * Cell名を設定.
    // * @param value Cell名
    // */
    /**
     * This method sets the Cell Name.
     * @param value CellName
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * CellのURLを取得する.
    // * @return 取得した CellのURL
    // */
    /**
     * This method creates and returns the URL for performing Cell related operations.
     * @return CellURL value
     * @throws DaoException DaoException
     */
    public String getUrl() throws DaoException {
        String normalizedUrl = url;
        if (!url.endsWith("/")) {
            normalizedUrl = url + "/";
        }
        return normalizedUrl;
    }

    // /**
    // * アクセストークンを取得.
    // * @return アクセストークン
    // * @throws DaoException DAO例外
    // */
    /**
     * This method returns the access token.
     * @return Access Token
     * @throws DaoException Exception thrown
     */
    public String getAccessToken() throws DaoException {
        if (this.accessor.getAccessToken() != null) {
            return this.accessor.getAccessToken();
        } else {
            throw DaoException.create("Unauthorized", HttpStatus.SC_UNAUTHORIZED);
        }
    }

    // /**
    // * アクセストークンの有効期限を取得.
    // * @return アクセストークンの有効期限
    // */
    /**
     * This method returns the expiration date of the access token.
     * @return Expiration date of the access token
     */
    public Number getExpiresIn() {
        return this.accessor.getExpiresIn();
    }

    // /**
    // * アクセストークンのタイプを取得.
    // * @return アクセストークンのタイプ
    // */
    /**
     * This method returns the token type.
     * @return Token Type
     */
    public String getTokenType() {
        return this.accessor.getTokenType();
    }

    // /**
    // * リフレッシュトークンを取得.
    // * @return リフレッシュトークン
    // * @throws DaoException DAO例外
    // */
    /**
     * This method returns the refresh token.
     * @return Refresh Token value
     * @throws DaoException Exception thrown
     */
    public String getRefreshToken() throws DaoException {
        if (this.accessor.getRefreshToken() != null) {
            return this.accessor.getRefreshToken();
        } else {
            throw DaoException.create("Unauthorized", HttpStatus.SC_UNAUTHORIZED);
        }
    }

    // /**
    // * リフレッシュの有効期限を取得.
    // * @return リフレッシュトークンの有効期限
    // */
    /**
     * This method gets the expiration date of the refresh token.
     * @return Expiration date of the refresh token
     */
    public Number getRefreshExpiresIn() {
        return this.accessor.getRefreshExpiresIn();
    }

    /**
     * Thismethod returns the location.
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    // /**
    // * CellのownerRepresentativeAccountsを設定.
    // * @param user アカウント名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method sets the ownerRepresentativeAccounts of Cell.
     * @param user Account Name
     * @throws DaoException Exception thrown
     */
    public void setOwnerRepresentativeAccounts(String user) throws DaoException {
        String value;
        value = "<p:account>" + user + "</p:account>";
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.proppatch(this.getUrl(), "p:ownerRepresentativeAccounts", value);
    }

    // /**
    // * CellのownerRepresentativeAccountsを設定(複数アカウント登録).
    // * @param accountName アカウント名の配列
    // * @throws DaoException DAO例外
    // */
    /**
     * This method sets the ownerRepresentativeAccounts of Cell(multiple sign up for an account).
     * @param accountName Account Name
     * @throws DaoException Exception thrown
     */
    public void setOwnerRepresentativeAccounts(String[] accountName) throws DaoException {
        StringBuilder sb = new StringBuilder();
        for (Object an : accountName) {
            sb.append("<p:account>");
            sb.append(an);
            sb.append("</p:account>");
        }
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.proppatch(this.getUrl(), "p:ownerRepresentativeAccounts", sb.toString());
    }

    /**
     * It creates and returns the class to access the Box. The Box corresponding to the schema that has been
     * authenticated if Accessor is a schema authenticated, Otherwise, it returns a Box of schema that is specified in
     * the personiumContext of this cell. When there is no corresponding Box, it throws an exception. Exception:
     * <ul>
     * <li>If the Box the corresponding schema authenticated does not exist.</li>
     * <li>If the schema is not defined in the personiumContext without authentication schema.</li>
     * <li>If the Box that corresponds to the schema defined in the personiumContext without authentication schema
     * can not be found.</li>
     * </ul>
     * @return Box object
     * @throws DaoException Exception thrown
     */
    public Box box() throws DaoException {
        /** obtain schemaUrl */
        PersoniumContext context = this.accessor.getContext();
        String schemaUrlFromAuthzHeader = this.accessor.getSchema();
        String schemaUrlFromContext = context.getBoxSchema();

        /**
         * If the URL of this Cell equals to the one set in the personiumContext,
         * return the box configured in the personiumContext without the following URL discovery process.
         */
        if (url != null && getUrl().equals(context.getCurrentCellUrl())) {
            return box(context.getBoxName(), context.getBoxSchema());
        }

        /** Discover the Box Url from Authorization Header or from Box Schema Url */
        String schemaUrl = null;
        String boxAccessUrl = null;
        if (schemaUrlFromAuthzHeader != null) {
            boxAccessUrl = UrlUtils.append(this.getUrl(), "__box");
            schemaUrl = schemaUrlFromAuthzHeader;
        } else if (schemaUrlFromContext != null) {
            boxAccessUrl = UrlUtils.append(this.getUrl(), "__box?schema=" + Utils.escapeURI(schemaUrlFromContext));
            schemaUrl = schemaUrlFromContext;
        } else {
            throw new DaoException("Cannot specify the box.");
        }
        Accessor tmpAccessor = this.accessor.clone();
        IRestAdapter rest = RestAdapterFactory.create(tmpAccessor);
        PersoniumResponse resp = rest.get(boxAccessUrl, RestAdapter.CONTENT_TYPE_JSON);
        String locationHeader = resp.getHeader(HttpHeaders.LOCATION);
        /** Extract the box name from box URL */
        String[] params = locationHeader.split("/");
        String boxName = null;
        if (locationHeader.endsWith("/")) {
            boxName = params[params.length - 2];
        } else {
            boxName = params[params.length - 1];
        }
        return box(boxName, schemaUrl);
    }

    // /**
    // * Boxへアクセスするためのクラスを生成.
    // * @param param Box名、schema名
    // * @return 生成したBoxインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * It creates and returns the class to access the Box for the specified Box name.
     * @param param Box Name
     * @return Box object
     * @throws DaoException Exception thrown
     */
    public Box box(String param) throws DaoException {
        String boxName = param;
        if (boxName.startsWith("http://") || boxName.startsWith("https://")) {
            // SchemaからBox名を引く
            /** Fetch Box name from Box schema. */
            String boxAccessUrl = UrlUtils.append(this.getUrl(), "__box?schema=" + Utils.escapeURI(param));
            Accessor tmpAccessor = this.accessor.clone();
            IRestAdapter rest = RestAdapterFactory.create(tmpAccessor);
            PersoniumResponse resp = rest.get(boxAccessUrl, RestAdapter.CONTENT_TYPE_JSON);
            String locationHeader = resp.getHeader(HttpHeaders.LOCATION);
            String[] params = locationHeader.split("/");
            if (locationHeader.endsWith("/")) {
                boxName = params[params.length - 2];
            } else {
                boxName = params[params.length - 1];
            }
        }

        this.accessor.setBoxName(boxName);
        String boxAccessUrl = UrlUtils.append(this.accessor.getCurrentCell().getUrl(), accessor.getBoxName());
        return new Box(this.accessor, boxName, "", boxAccessUrl);
    }

    // /**
    // * Boxへアクセスするためのクラスを生成.
    // * @param boxName Box Name
    // * @param schemaValue スキーマ名
    // * @return 生成したBoxインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * It creates and returns the class to access the Box for the specified Box name and Box schema.
     * @param boxName Box Name
     * @param schemaValue BoxSchema
     * @return Box object
     * @throws DaoException Exception thrown
     */
    public Box box(String boxName, String schemaValue) throws DaoException {
        this.accessor.setBoxName(boxName);
        String boxAccessUrl = UrlUtils.append(this.accessor.getCurrentCell().getUrl(), accessor.getBoxName());
        return new Box(this.accessor, boxName, schemaValue, boxAccessUrl);
    }

    // /**
    // * BaseUrl を取得.
    // * @return baseUrl 基底URL文字列
    // */
    /**
     * This method returns the BaseURl in string form.
     * @return baseUrl BaseURL.
     */
    public String getBaseUrlString() {
        return accessor.getBaseUrl();
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key for Box.
     * @return OData Key
     */
    public String getKey() {
        return String.format("('%s')", this.name);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the classname in camel case.
     * @return ClassName
     */
    public String getClassName() {
        return CLASSNAME;
    }

    /**
     * This method is used for installing a box using a bar file.
     * @param boxName desired name of the box to be installed.
     * @param barFile input stream of actual bar file
     * @return Box response
     * @throws DaoException thrown when installation is rejected for some reason.
     */
    public Box installBox(String boxName, InputStream barFile) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        Box targetBox = this.box(boxName, null);
        rest.mkcol(targetBox.url.toString(), barFile, IRestAdapter.CONTENT_TYPE_ZIP);
        /** if an error response is returned in HTTP, DaoException will be thrown. */
        return targetBox;
    }

    /**
     * This method is used for installing a box using a bar file URL.
     * @param boxName desired name of the box to be installed.
     * @param barFileUrl URL of the bar file publicly available.
     * @return Box response
     * @throws DaoException thrown when installation is rejected for some reason.
     */
    public Box installBox(String boxName, String barFileUrl) throws DaoException {
        PersoniumResponse res = RestAdapterFactory.create(this.accessor).get(barFileUrl, "application/octet-stream");
        InputStream barFile = res.bodyAsStream();
        return this.installBox(boxName, barFile);
    }
}
