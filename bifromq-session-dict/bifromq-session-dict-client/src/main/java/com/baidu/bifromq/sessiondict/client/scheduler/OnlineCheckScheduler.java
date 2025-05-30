/*
 * Copyright (c) 2025. The BifroMQ Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.baidu.bifromq.sessiondict.client.scheduler;

import static com.baidu.bifromq.sessiondict.SessionRegisterKeyUtil.parseTenantId;

import com.baidu.bifromq.baserpc.client.IRPCClient;
import com.baidu.bifromq.basescheduler.BatchCallScheduler;
import com.baidu.bifromq.basescheduler.IBatchCall;
import com.baidu.bifromq.basescheduler.IBatchCallBuilder;
import com.baidu.bifromq.sessiondict.SessionRegisterKeyUtil;
import com.baidu.bifromq.sessiondict.client.type.OnlineCheckRequest;
import com.baidu.bifromq.sessiondict.client.type.OnlineCheckResult;
import com.baidu.bifromq.sessiondict.rpc.proto.ExistReply;
import com.baidu.bifromq.sessiondict.rpc.proto.SessionDictServiceGrpc;
import com.baidu.bifromq.sysprops.props.DataPlaneMaxBurstLatencyMillis;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

/**
 * Scheduler for checking the online status of a session.
 */
public class OnlineCheckScheduler extends BatchCallScheduler<OnlineCheckRequest, OnlineCheckResult, String>
    implements IOnlineCheckScheduler {

    public OnlineCheckScheduler(IRPCClient rpcClient) {
        super((name, batcherKey) -> new IBatchCallBuilder<>() {
                private final IRPCClient.IRequestPipeline<com.baidu.bifromq.sessiondict.rpc.proto.ExistRequest, ExistReply> ppln
                    = rpcClient.createRequestPipeline(parseTenantId(batcherKey), null, batcherKey,
                    Collections.emptyMap(), SessionDictServiceGrpc.getExistMethod());

                @Override
                public IBatchCall<OnlineCheckRequest, OnlineCheckResult, String> newBatchCall() {
                    return new BatchSessionExistCall(ppln);
                }

                @Override
                public void close() {
                    ppln.close();
                }
            },
            Duration.ofMillis(DataPlaneMaxBurstLatencyMillis.INSTANCE.get()).toNanos());
    }

    @Override
    protected Optional<String> find(OnlineCheckRequest call) {
        return Optional.of(SessionRegisterKeyUtil.toRegisterKey(call.tenantId(), call.userId(), call.clientId()));
    }
}
