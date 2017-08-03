/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.core.query.output.callback;

import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.state.StateEvent;
import org.wso2.siddhi.core.event.state.StateEventPool;
import org.wso2.siddhi.core.event.stream.StreamEventPool;
import org.wso2.siddhi.core.event.stream.converter.StreamEventConverter;
import org.wso2.siddhi.core.table.CompiledUpdateSet;
import org.wso2.siddhi.core.table.Table;
import org.wso2.siddhi.core.util.collection.operator.CompiledExpression;

/**
 * Implementation of {@link OutputCallback} to receive processed Siddhi events from
 * Siddhi queries and update data into a {@link Table}
 * based on received events and condition.
 */
public class UpdateTableCallback extends OutputCallback {
    private final int matchingStreamIndex;
    private Table table;
    private CompiledExpression compiledExpression;
    private CompiledUpdateSet compiledUpdateSet;
    private boolean convertToStreamEvent;
    private StateEventPool stateEventPool;
    private StreamEventPool streamEventPool;
    private StreamEventConverter streamEventConvertor;

    public UpdateTableCallback(Table table, CompiledExpression compiledExpression, CompiledUpdateSet compiledUpdateSet,
                               int matchingStreamIndex, boolean convertToStreamEvent, StateEventPool stateEventPool,
                               StreamEventPool streamEventPool, StreamEventConverter streamEventConvertor) {
        this.table = table;
        this.compiledExpression = compiledExpression;
        this.compiledUpdateSet = compiledUpdateSet;
        this.matchingStreamIndex = matchingStreamIndex;
        this.convertToStreamEvent = convertToStreamEvent;
        this.stateEventPool = stateEventPool;
        this.streamEventPool = streamEventPool;
        this.streamEventConvertor = streamEventConvertor;
    }

    @Override
    public synchronized void send(ComplexEventChunk updatingEventChunk) {
        updatingEventChunk.reset();
        if (updatingEventChunk.hasNext()) {
            ComplexEventChunk<StateEvent> updatingStateEventChunk = constructMatchingStateEventChunk(updatingEventChunk,
                    convertToStreamEvent, stateEventPool, matchingStreamIndex, streamEventPool, streamEventConvertor);
            table.updateEvents(updatingStateEventChunk, compiledExpression, compiledUpdateSet);
        }
    }

}
