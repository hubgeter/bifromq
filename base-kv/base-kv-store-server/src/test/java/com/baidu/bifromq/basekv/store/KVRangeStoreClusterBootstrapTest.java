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

package com.baidu.bifromq.basekv.store;

import static com.baidu.bifromq.basekv.utils.BoundaryUtil.FULL_BOUNDARY;
import static org.awaitility.Awaitility.await;

import com.baidu.bifromq.basekv.annotation.Cluster;
import com.baidu.bifromq.basekv.proto.KVRangeId;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class KVRangeStoreClusterBootstrapTest extends KVRangeStoreClusterTestTemplate {

    @Cluster(initVoters = 1)
    @Test(groups = "integration")
    public void testBootstrap1StoreCluster() {
        KVRangeId rangeId = cluster.genesisKVRangeId();
        await().until(() -> {
            KVRangeConfig rangeSetting = cluster.kvRangeSetting(rangeId);
            if (rangeSetting == null) {
                return false;
            }
            return rangeSetting.clusterConfig.getVotersCount() == 1;
        });
    }

    @Cluster(initVoters = 2)
    @Test(groups = "integration")
    public void testBootstrap2StoreCluster() {
        KVRangeId rangeId = cluster.genesisKVRangeId();
        await().until(() -> {
            KVRangeConfig rangeSetting = cluster.kvRangeSetting(rangeId);
            if (rangeSetting == null) {
                return false;
            }
            return rangeSetting.ver >= 2 &&
                FULL_BOUNDARY.equals(rangeSetting.boundary) && rangeSetting.clusterConfig.getVotersCount() == 2;
        });
    }

    @Test(groups = "integration")
    public void testBootstrap3StoreCluster() {
        KVRangeId rangeId = cluster.genesisKVRangeId();
        await().until(() -> {
            KVRangeConfig rangeSetting = cluster.kvRangeSetting(rangeId);
            if (rangeSetting == null) {
                return false;
            }
            return rangeSetting.ver >= 2 &&
                FULL_BOUNDARY.equals(rangeSetting.boundary) && rangeSetting.clusterConfig.getVotersCount() == 3;
        });
    }
}
