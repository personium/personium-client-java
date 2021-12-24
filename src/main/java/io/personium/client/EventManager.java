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

import java.util.HashMap;

import org.json.simple.JSONObject;

import io.personium.client.http.IRestAdapter;
import io.personium.client.http.PersoniumResponse;
import io.personium.client.http.RestAdapter;
import io.personium.client.http.RestAdapterFactory;

/**
 * It creates a new object of EventManager. This class performs the CRUD operations for Event object.
 */
public class EventManager {
    /** Reference to Accessor. */
    Accessor accessor;

    /**
     * This is the parameterized constructor with one argument initializing the accessor.
     * @param as Accessor
     */
    public EventManager(Accessor as) {
        this.accessor = as.clone();
    }

    /**
     * This method is used to register the event using Event object.
     * @param obj Event object
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public void post(Event obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Type", obj.getType());
        body.put("Object", obj.getObject());
        body.put("Info", obj.getInfo());
        this.post(body);
    }

    /**
     * This method is used to register the event using request body.
     * @param body Request Body
     * @return PersoniumResponse
     * @throws DaoException Exception thrown
     */
    public PersoniumResponse post(JSONObject body) throws DaoException {
        return this.post(body, null);
    }

    /**
     * This method is used to register the event using request body.
     * @param body Request Body
     * @param dcRequestKey X-Personium-RequestKey header
     * @return PersoniumResponse
     * @throws DaoException Exception thrown
     */
    public PersoniumResponse post(JSONObject body, String dcRequestKey) throws DaoException {
        String url = this.getEventUrl();
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        HashMap<String, String> header = new HashMap<String, String>();
        if (dcRequestKey != null) {
            header.put("X-Personium-RequestKey", dcRequestKey);
        }
        return rest.post(url, header, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
    }

    /**
     * This method is used to register the event using type, object and info.
     * @param type Type Event
     * @param object Object Event
     * @param info Info Event
     * @throws DaoException Exception thrown
     */
    public void post(String type, String object, String info) throws DaoException {
        JSONObject body = makeLogBody(type, object, info);
        this.post(body, null);
    }

    /**
     * This method is used to register the event using type, object, info and dcRequestKey.
     * @param type Type Events
     * @param object Object Event
     * @param info Info Event
     * @param dcRequestKey X-Personium-RequestKey Header
     * @throws DaoException Exception thrown
     */
    public void post(String type, String object, String info, String dcRequestKey)
            throws DaoException {
        JSONObject body = makeLogBody(type, object, info);
        this.post(body, dcRequestKey);
    }

    /**
     * This method generates and returns the Event URL.
     * @return URL value
     * @throws DaoException DaoException
     */
    protected String getEventUrl() throws DaoException {
        StringBuilder sb = new StringBuilder(this.accessor.getCurrentCell().getUrl());
        sb.append("__event");
        return sb.toString();
    }

    /**
     * This method creates Log Body in the form of JSONObject.
     * @param type Type Events
     * @param object Object Event
     * @param info Info Event
     * @return
     */
    @SuppressWarnings("unchecked")
    private JSONObject makeLogBody(String type, String object, String info) {
        JSONObject body = new JSONObject();
        body.put("Type", type);
        body.put("Object", object);
        body.put("Info", info);
        return body;
    }

}
