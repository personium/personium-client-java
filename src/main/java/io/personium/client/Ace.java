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

import java.util.ArrayList;
import java.util.List;

/**
 * It creates a new object of Ace. This class represents Ace (Access Control Element) in WebDAV ACL implemented in PCS.
 */
public class Ace {

    /** Target Principal . */
    Principal principal;
    /** Granted Privileges List for this Ace. */
    List<String> privilegeList;

    /**
     * This is the default constructor calling its parent constructor and initializing the privilegeList.
     */
    public Ace() {
        super();
        this.privilegeList = new ArrayList<String>();
    }

    /**
     * This method gets the Principal (typically a role) of this Ace.
     * @return Principal of this Ace.
     */
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     * This method sets the Principal (typically a role in PCS) for this Ace.
     * @param principal instance of principal
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * This method adds a privilege to the privilegeList.
     * @param value privilege name
     */
    public void addPrivilege(String value) {
        this.privilegeList.add(value);
    }

    /**
     * This method returns privilege list.
     * @return list of privileges
     */
    public List<String> getPrivilegeList() {
        return this.privilegeList;
    }
}
