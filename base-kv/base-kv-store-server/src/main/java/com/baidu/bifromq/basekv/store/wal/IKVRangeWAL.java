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

package com.baidu.bifromq.basekv.store.wal;

import com.baidu.bifromq.basekv.proto.KVRangeCommand;
import com.baidu.bifromq.basekv.proto.KVRangeId;
import com.baidu.bifromq.basekv.proto.KVRangeSnapshot;
import com.baidu.bifromq.basekv.raft.IRaftNode;
import com.baidu.bifromq.basekv.raft.event.ElectionEvent;
import com.baidu.bifromq.basekv.raft.proto.ClusterConfig;
import com.baidu.bifromq.basekv.raft.proto.LogEntry;
import com.baidu.bifromq.basekv.raft.proto.RaftMessage;
import com.baidu.bifromq.basekv.raft.proto.RaftNodeStatus;
import com.baidu.bifromq.basekv.raft.proto.RaftNodeSyncState;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.reactivex.rxjava3.core.Observable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public interface IKVRangeWAL {
    String storeId();

    KVRangeId rangeId();

    boolean isLeader();

    Optional<String> currentLeader();

    RaftNodeStatus currentState();

    Observable<RaftNodeStatus> state();

    Observable<ElectionEvent> election();

    KVRangeSnapshot latestSnapshot();

    ClusterConfig latestClusterConfig();

    CompletableFuture<Long> propose(KVRangeCommand command);

    Observable<Map<String, RaftNodeSyncState>> replicationStatus();

    IKVRangeWALSubscription subscribe(long lastFetchedIndex, IKVRangeWALSubscriber subscriber, Executor executor);

    CompletableFuture<LogEntry> once(long lastFetchedIndex, Predicate<LogEntry> condition, Executor executor);

    Observable<Long> commitIndex();

    CompletableFuture<Iterator<LogEntry>> retrieveCommitted(long fromIndex, long maxSize);

    CompletableFuture<Long> readIndex();

    CompletableFuture<Void> transferLeadership(String peerId);

    boolean stepDown();

    CompletableFuture<Void> changeClusterConfig(String correlateId, Set<String> voters, Set<String> learners);

    CompletableFuture<Void> compact(KVRangeSnapshot snapshot);

    Observable<RestoreSnapshotTask> snapshotRestoreTask();

    Observable<Map<String, List<RaftMessage>>> peerMessages();

    CompletableFuture<Void> recover();

    void receivePeerMessages(String peerId, List<RaftMessage> messages);

    long logDataSize();

    void tick();

    void start();

    CompletableFuture<Void> close();

    CompletableFuture<Void> destroy();

    class RestoreSnapshotTask {
        public final KVRangeSnapshot snapshot;
        public final String leader;
        private final IRaftNode.IAfterInstalledCallback callback;

        public RestoreSnapshotTask(ByteString snapshotData,
                                   String leader,
                                   IRaftNode.IAfterInstalledCallback callback) {
            this.leader = leader;
            this.callback = callback;
            KVRangeSnapshot ss = null;
            try {
                ss = KVRangeSnapshot.parseFrom(snapshotData);
            } catch (InvalidProtocolBufferException e) {
                this.callback.call(null, e);
            }
            snapshot = ss;
        }

        public CompletableFuture<Void> afterRestored(KVRangeSnapshot snapshot, Throwable ex) {
            return callback.call(snapshot == null ? null : snapshot.toByteString(), ex);
        }
    }
}
