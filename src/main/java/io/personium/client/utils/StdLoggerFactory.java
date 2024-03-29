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

///**
// * 標準出力ログファクトリークラス.
// */
/**
 * This is the Standard output log factory class.
 */
public class StdLoggerFactory extends PersoniumLoggerFactory {

    /**
     * This method is used for new instantiation of StdLogger.
     * @param clazz Class
     * @return PersoniumLogger Personium Log
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected PersoniumLogger newInstance(Class clazz) {
        return new StdLogger(clazz);
    }
}
