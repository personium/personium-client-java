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
package io.personium.client.utils;

/**
 * This is the abstract Personium log factory class.
 */
public abstract class PersoniumLoggerFactory {
    private static PersoniumLoggerFactory personiumLoggerFactory = new StdLoggerFactory();

    // /**
    // * デフォルトログファクトリー設定.
    // * @param loggerFactory ログファクトリー
    // */
    /**
     * This method is used for Default log factory setting.
     * @param loggerFactory Log Factory
     */
    public static void setDefaultFactory(PersoniumLoggerFactory loggerFactory) {
        personiumLoggerFactory = loggerFactory;
    }

    /**
     * This method is used for Log factory acquisition.
     * @param clazz class
     * @return PersoniumLogger Personium log
     */
    @SuppressWarnings("rawtypes")
    public static PersoniumLogger getLogger(Class clazz) {
        return personiumLoggerFactory.newInstance(clazz);
    }

    /**
     * This is the declaration for new instantiation.
     * @param clazz Class
     * @return PersoniumLogger Personium Log
     */
    @SuppressWarnings("rawtypes")
    protected abstract PersoniumLogger newInstance(Class clazz);
}
