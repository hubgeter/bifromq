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

package com.baidu.bifromq.sysprops.props;

import com.baidu.bifromq.sysprops.BifroMQSysProp;
import com.baidu.bifromq.sysprops.parser.IntegerParser;

/**
 * The timeout seconds to consider persistent session is probably detached from mqtt client.
 */
public final class PersistentSessionDetachTimeoutSecond extends BifroMQSysProp<Integer, IntegerParser> {
    public static final PersistentSessionDetachTimeoutSecond INSTANCE = new PersistentSessionDetachTimeoutSecond();

    private PersistentSessionDetachTimeoutSecond() {
        super("persistent_session_detach_timeout_second", 7200, IntegerParser.from(60, Integer.MAX_VALUE));
    }
}
