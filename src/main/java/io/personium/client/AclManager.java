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

import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;

///**
// * ACLのCRUDを行うためのクラス.
// */
/**
 * It creates a new object of AclManager. This class performs the CRUD operations for ACL.
 */
public class AclManager {
    private Accessor accessor;
    // /** DAVコレクション. */
    /** DAV Collection. */
    PersoniumCollection collection;

    private String cellUrl;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor having only one parameter and initializing the class variable accessor.
     * @param as Accessor
     */
    public AclManager(Accessor as) {
        this.accessor = as;
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param name Cell名
    // */
    /**
     * This is the parameterized constructor having two parameters initializing the class variable accessor and
     * cellURL.
     * @param as Accessor
     * @param url Cell URL
     */
    public AclManager(Accessor as, String url) {
        this.accessor = as;
        this.cellUrl = url;
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param dav 対象となるDAVコレクション
    // */
    /**
     * This is the parameterized constructor having two parameters initializing the class variable accessor and
     * DavCollection.
     * @param as Accessor
     * @param dav DavCollection object
     */
    public AclManager(Accessor as, DavCollection dav) {
        this(as);
        this.collection = dav;
    }

    // /**
    // * ACLを登録する.
    // * @param body リクエストボディ(XML形式)
    // * @throws DaoException DAO例外
    // */
    /**
     * This method registers the AC as stringL.
     * @param body request body (XML format)
     * @throws DaoException Exception thrown
     */
    public void set(String body) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(accessor);
        rest.acl(this.getUrl(), body);
    }

    // /**
    // * ACLオブジェクトとしてACLをセットする.
    // * @param obj Aclオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method registers the ACL as ACL object.
     * @param obj Acl object
     * @throws DaoException Exception thrown
     */
    public void set(Acl obj) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(accessor);
        rest.acl(this.getUrl(), obj.toXmlString());
    }

    // /**
    // * ACL情報をAclオブジェクトとして取得.
    // * @return Aclオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method gets ACL information as ACL object.
     * @return Acl object
     * @throws DaoException Exception thrown
     */
    public Acl get() throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(accessor);
        PersoniumResponse res = rest.propfind(this.getUrl(), "0");
        return Acl.parse(res.bodyAsString());
    }

    // /**
    // * URLを生成.
    // * @return 現在のコレクションへのURL
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates the URL for performing ACL operation.
     * @return URL to a collection
     * @throws DaoException Exception thrown
     */
    private String getUrl() throws DaoException {
        if (this.collection != null) {
            return this.collection.getPath();
        } else {
            return this.cellUrl;
        }
    }
}
