/*
 * Copyright (c) 2024. The BifroMQ Authors. All Rights Reserved.
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

package com.baidu.bifromq.starter.config.model.dist;

import com.baidu.bifromq.starter.config.model.BalancerOptions;
import com.baidu.bifromq.starter.config.model.RocksDBEngineConfig;
import com.baidu.bifromq.starter.config.model.StorageEngineConfig;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistWorkerConfig {
    private boolean enable = true;
    // 0 for doing tasks on calling threads
    private int workerThreads = 0;
    private int tickerThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 20);
    private int maxWALFetchSize = 10 * 1024 * 1024; // 10MB
    private int compactWALThreshold = 2500;
    private int gcIntervalSeconds = 86400; // every 24 hours
    @JsonSetter(nulls = Nulls.SKIP)
    private StorageEngineConfig dataEngineConfig = new RocksDBEngineConfig();
    @JsonSetter(nulls = Nulls.SKIP)
    private StorageEngineConfig walEngineConfig = new RocksDBEngineConfig()
        .setManualCompaction(true)
        .setCompactMinTombstoneKeys(2500)
        .setCompactMinTombstoneRanges(2);
    @JsonSetter(nulls = Nulls.SKIP)
    private BalancerOptions balanceConfig = new BalancerOptions();
    @JsonSetter(nulls = Nulls.SKIP)
    private Map<String, String> attributes = new HashMap<>();

    public DistWorkerConfig() {
        balanceConfig.getBalancers().put("com.baidu.bifromq.dist.worker.balance.RangeLeaderBalancerFactory",
            Struct.getDefaultInstance());
        balanceConfig.getBalancers().put("com.baidu.bifromq.dist.worker.balance.ReplicaCntBalancerFactory",
            Struct.newBuilder()
                .putFields("votersPerRange", Value.newBuilder().setNumberValue(3).build())
                .putFields("learnersPerRange", Value.newBuilder().setNumberValue(-1).build())
                .build());
    }
}
