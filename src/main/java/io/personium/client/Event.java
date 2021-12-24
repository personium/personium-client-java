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

import org.json.simple.JSONObject;

/**
 * It creates a new object of Event. This class represents Event object.
 */
public class Event {
    /** Variable Type. */
    String type = "";
    /** Variable Object. */
    String object = "";
    /** Variable Info. */
    String info = "";

    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public Event() {
        super();
    }

    /**
     * This method returns the Type value.
     * @return type value
     */
    public String getType() {
        return type;
    }

    /**
     * This method sets the type value.
     * @param value type
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * This method returns the object value.
     * @return object value
     */
    public String getObject() {
        return object;
    }

    /**
     * This method sets the object value.
     * @param value object
     */
    public void setObject(String value) {
        this.object = value;
    }

    /**
     * This method returns the info value.
     * @return info value
     */
    public String getInfo() {
        return info;
    }

    /**
     * This method sets the info value.
     * @param value info
     */
    public void setInfo(String value) {
        this.info = value;
    }

    /**
     * This method creates a new JSON object for Event.
     * @return JSON object
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("Type", this.type);
        json.put("Object", this.object);
        json.put("Info", this.info);
        return json;
    }
}
