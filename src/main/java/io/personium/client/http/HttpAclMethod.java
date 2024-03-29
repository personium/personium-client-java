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

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

///**
// * ACLメソッド定義.
// */
/**
 * It creates a new object of HttpAclMethod. This class is the ACL method definition class.
 */
public class HttpAclMethod extends HttpEntityEnclosingRequestBase {
    // /** メソッド名定義. */
    /** Method Name. */
    private static final String METHOD_NAME = "ACL";

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor.
     */
    public HttpAclMethod() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param uri 対象URLをセットしたURLオブジェクト
    // */
    /**
     * This is the parameterized constructor calling its parent constructor and setting URI.
     * @param uri URL object that set the target URL
     */
    public HttpAclMethod(final URI uri) {
        super();
        setURI(uri);
    }

    // /**
    // * コンストラクタ.
    // * @param uri 対象URL文字列
    // */
    /**
     * This is the parameterized constructor calling its parent constructor and setting URI after formatting it.
     * @param uri Target URL string
     */
    public HttpAclMethod(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    /**
     * This method returns the method name.
     * @return Method name value
     */
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
