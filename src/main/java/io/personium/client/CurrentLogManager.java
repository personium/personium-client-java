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

///**
// * EventLog取得のためのクラス.
// */
/**
 * This is the class for EventLog acquisition.
 */
public class CurrentLogManager extends LogManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     */
    public CurrentLogManager(Accessor as) {
        super(as);
    }

    /**
     * This method generates and returns the Log URL for the specified filename.
     * @param filename value
     * @return URL value
     * @throws DaoException DaoException
     */
    @Override
    protected String getLogUrl(String filename) throws DaoException {
        StringBuilder sb = new StringBuilder(super.accessor.getCurrentCell().getUrl());
        sb.append("__log/current/");
        sb.append(filename);
        return sb.toString();
    }

}
