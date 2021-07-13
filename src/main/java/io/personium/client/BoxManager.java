/**
 * Personium
 * Copyright 2014-2021 - Personium Project Authors
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

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;
import io.personium.client.utils.UrlUtils;
import io.personium.client.utils.Utils;

///**
// * BoxのCRUDのためのクラス.
// */
/**
 * It creates a new object of BoxManager. This class performs CRUD operations for Box.
 */
public class BoxManager extends ODataManager {
    // /**
    // * コンストラクタ.
    // * @param as Accessorオブジェクト
    // */
    /**
     * This is the parameterized constructor with one parameter calling its parent constructor.
     * @param as Accessor
     */
    public BoxManager(Accessor as) {
        super(as);
    }

    /**
     * This method is used to generate the URL for performing Box related operations.
     * @return URL value
     * @throws DaoException DaoException
     */
    @Override
    public String getUrl() throws DaoException {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/Box");
        return sb.toString();
    }

    // /**
    // * Boxを作成.
    // * @param obj Boxオブジェクト
    // * @return Boxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create a Box from Box object.
     * @param obj Box object
     * @return Box object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Box create(Box obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("Schema", obj.getSchema());
        JSONObject json = internalCreate(body);
        String path = UrlUtils.append(accessor.getCurrentCell().getUrl(), (String) body.get("Name"));
        obj.initialize(this.accessor, json, path);
        return obj;
    }

    // /**
    // * Boxを作成.
    // * @param body リクエストボディ
    // * @return 作成したBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create a Box from request body.
     * @param body Request Body
     * @return Box object
     * @throws DaoException Exception thrown
     */
    public Box create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = internalCreate(body);
        return new Box(accessor, json, UrlUtils.append(accessor.getCurrentCell().getUrl(), (String) body.get("Name")));
    }

    // /**
    // * Boxを取得.
    // * @param name 取得対象のbox名
    // * @return 取得したしたBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to retrieve box details for the specified box.
     * @param name Box Name
     * @return Box object
     * @throws DaoException Exception thrown
     */
    public Box retrieve(String name) throws DaoException {
        JSONObject json = internalRetrieve(name);
        return new Box(accessor, json, UrlUtils.append(accessor.getCurrentCell().getUrl(), name));
    }

    /**
     * This method performs recursively delete box
     * @param boxName boxName to be delete
     * @return PersoniumResponse 
     * @throws DaoException Library Exception
     */
    public PersoniumResponse recursiveDelete(String boxName) throws DaoException {
        String url = UrlUtils.append(accessor.getCurrentCell().getUrl(), Utils.escapeURI(boxName));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Personium-Recursive", "true");
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        return rest.del(url, headers);
    }

}
