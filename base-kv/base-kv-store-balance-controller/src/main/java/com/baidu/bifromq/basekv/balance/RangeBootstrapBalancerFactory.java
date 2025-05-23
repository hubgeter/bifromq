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

package com.baidu.bifromq.basekv.balance;

import com.baidu.bifromq.basekv.balance.impl.RangeBootstrapBalancer;
import java.time.Duration;

/**
 * Built-in balancer for range bootstrap.
 */
class RangeBootstrapBalancerFactory implements IStoreBalancerFactory {

    private final Duration delay;

    public RangeBootstrapBalancerFactory(Duration delay) {
        this.delay = delay;
    }

    @Override
    public StoreBalancer newBalancer(String clusterId, String localStoreId) {
        return new RangeBootstrapBalancer(clusterId, localStoreId, delay);
    }
}
