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

package com.baidu.bifromq.sessiondict;

import static com.baidu.bifromq.type.MQTTClientInfoConstants.MQTT_CLIENT_ID_KEY;
import static com.baidu.bifromq.type.MQTTClientInfoConstants.MQTT_USER_ID_KEY;

import com.baidu.bifromq.sysprops.props.SessionRegisterNumber;
import com.baidu.bifromq.type.ClientInfo;
import java.util.Objects;

public class SessionRegisterKeyUtil {
    private static final int SESSION_REGISTER_NUM = SessionRegisterNumber.INSTANCE.get();

    public static String toRegisterKey(ClientInfo owner) {
        return toRegisterKey(owner.getTenantId(),
            owner.getMetadataOrDefault(MQTT_USER_ID_KEY, ""),
            owner.getMetadataOrDefault(MQTT_CLIENT_ID_KEY, ""));
    }

    public static String toRegisterKey(String tenantId, String userId, String clientId) {
        int key = Objects.hash(userId, clientId) % SESSION_REGISTER_NUM;
        if (key < 0) {
            key += SESSION_REGISTER_NUM;
        }
        return tenantId + "_" + key;
    }

    public static String parseTenantId(String registerKey) {
        int index = registerKey.indexOf('_');
        if (index == -1) {
            throw new IllegalArgumentException("Invalid register key: " + registerKey);
        }
        return registerKey.substring(0, index);
    }
}
