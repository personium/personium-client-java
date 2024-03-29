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
package io.personium.client.http;

import org.json.simple.JSONObject;

import io.personium.client.DaoException;

///**
// * Batchのレスポンス型.
// */
/**
 * It creates a new object of PersoniumBatchResponse. This class represents the response class for Batch.
 */
public class PersoniumBatchRespose extends PersoniumResponse {

    // /**
    // * レスポンスボディをJSONで取得.
    // * @return JSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method returns the response body in JSON format.
     * @return JSONObject
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public final JSONObject bodyAsJson() throws DaoException {
        JSONObject results = new JSONObject();
        results.put("results", new JSONObject());
        JSONObject d = new JSONObject();
        d.put("d", results);
        return d;
    }

}
