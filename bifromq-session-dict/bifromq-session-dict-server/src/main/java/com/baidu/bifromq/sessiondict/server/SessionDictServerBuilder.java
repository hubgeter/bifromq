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

package com.baidu.bifromq.sessiondict.server;

import com.baidu.bifromq.baserpc.server.RPCServerBuilder;
import com.baidu.bifromq.mqtt.inbox.IMqttBrokerClient;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The builder for building Session Dict Server.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Accessors(fluent = true)
@Setter
public class SessionDictServerBuilder {
    RPCServerBuilder rpcServerBuilder;
    IMqttBrokerClient mqttBrokerClient;
    Map<String, String> attributes = new HashMap<>();
    Set<String> defaultGroupTags = new HashSet<>();
    int workerThreads;

    public ISessionDictServer build() {
        Preconditions.checkNotNull(rpcServerBuilder, "RPC Server Builder is null");
        return new SessionDictServer(this);
    }
}
