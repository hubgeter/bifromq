/*
 * Copyright (c) 2023. The BifroMQ Authors. All Rights Reserved.
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

package com.baidu.bifromq.sessiondict.client;

import com.baidu.bifromq.baserpc.client.IConnectable;
import com.baidu.bifromq.sessiondict.client.type.OnlineCheckRequest;
import com.baidu.bifromq.sessiondict.client.type.OnlineCheckResult;
import com.baidu.bifromq.sessiondict.rpc.proto.GetReply;
import com.baidu.bifromq.sessiondict.rpc.proto.GetRequest;
import com.baidu.bifromq.sessiondict.rpc.proto.KillAllReply;
import com.baidu.bifromq.sessiondict.rpc.proto.KillReply;
import com.baidu.bifromq.sessiondict.rpc.proto.ServerRedirection;
import com.baidu.bifromq.sessiondict.rpc.proto.SubReply;
import com.baidu.bifromq.sessiondict.rpc.proto.SubRequest;
import com.baidu.bifromq.sessiondict.rpc.proto.UnsubReply;
import com.baidu.bifromq.sessiondict.rpc.proto.UnsubRequest;
import com.baidu.bifromq.type.ClientInfo;
import jakarta.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public interface ISessionDictClient extends IConnectable, AutoCloseable {

    static SessionDictClientBuilder newBuilder() {
        return new SessionDictClientBuilder();
    }

    ISessionRegistration reg(ClientInfo owner, IKillListener killListener);

    CompletableFuture<KillReply> kill(long reqId, String tenantId, String userId, String clientId, ClientInfo killer,
                                      ServerRedirection redirection);

    CompletableFuture<KillAllReply> killAll(long reqId,
                                            String tenantId,
                                            @Nullable String userId,
                                            ClientInfo killer,
                                            ServerRedirection redirection);

    CompletableFuture<GetReply> get(GetRequest request);

    CompletableFuture<OnlineCheckResult> exist(OnlineCheckRequest onlineCheckRequest);

    CompletableFuture<SubReply> sub(SubRequest request);

    CompletableFuture<UnsubReply> unsub(UnsubRequest request);

    void close();

    interface IKillListener {
        void onKill(ClientInfo killer, ServerRedirection redirection);
    }
}
