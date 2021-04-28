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

///**
// * ユニット昇格後のAccessor.
// */
/**
 * It creates a new object of OwnerAccessor. This class represents Accessor of the unit after promotion.
 */
public class OwnerAccessor extends Accessor {

    // CHECKSTYLE:OFF
    /** ユニットレベルAPI へアクセスするためのクラスインスタンス。cell().unit でアクセス. */
    /** Class instance for access to the unit level API. */
    public UnitManager unit; // CHECKSTYLE IGNORE - It needs to be public for calls from engine.

    // CHECKSTYLE:ON

    // /**
    // * コンストラクタ.
    // * @param personiumContext PersoniumContext
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param personiumContext personiumContext object
     * @throws DaoException DaoException
     */
    public OwnerAccessor(PersoniumContext personiumContext) throws DaoException {
        super(personiumContext);
    }

    /**
     * This is the parameterized constructor with two arguments calling its parent constructor internally and setting
     * their class variables.
     * @param personiumContext personiumContext
     * @param as Accessor
     * @throws DaoException Exception thrown
     */
    public OwnerAccessor(PersoniumContext personiumContext, Accessor as) throws DaoException {
        super(personiumContext);
        this.setAccessToken(as.getAccessToken());
        this.setAccessType(as.getAccessType());
        this.setCell(as.getCellName());
        this.setUserId(as.getUserId());
        this.setPassword(as.getPassword());
        this.setSchema(as.getSchema());
        this.setSchemaUserId(as.getSchemaUserId());
        this.setSchemaPassword(as.getSchemaPassword());
        this.setTargetCellUrl(as.getTargetCellUrl());
        this.setTransCellToken(as.getTransCellToken());
        this.setTransCellRefreshToken(as.getTransCellRefreshToken());
        this.setBoxSchema(as.getBoxSchema());
        this.setBoxName(as.getBoxName());
        this.setBaseUrl(as.getBaseUrl());
        this.setContext(as.getContext());
        this.setCurrentCell(as.getCurrentCell());
        this.setDefaultHeaders(as.getDefaultHeaders());

        // Unit昇格
        /** Unit promotion. */
        this.owner = true;

        certification();

        this.unit = new UnitManager(this);
    }
}
