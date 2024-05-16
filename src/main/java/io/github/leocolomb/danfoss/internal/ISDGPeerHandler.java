package io.github.leocolomb.danfoss.internal;

import org.eclipse.jdt.annotation.NonNull;

import io.github.leocolomb.danfoss.internal.protocol.Dominion;

public interface ISDGPeerHandler {

    public void reportStatus(@NonNull String status, @NonNull String statusDetail, String description);

    public void handlePacket(Dominion.@NonNull Packet pkt);

    public void ping();
}
