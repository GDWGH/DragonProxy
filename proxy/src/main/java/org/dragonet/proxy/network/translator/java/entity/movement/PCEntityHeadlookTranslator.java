/*
 * DragonProxy
 * Copyright (C) 2016-2019 Dragonet Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can view the LICENSE file for more details.
 *
 * https://github.com/DragonetMC/DragonProxy
 */
package org.dragonet.proxy.network.translator.java.entity.movement;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import org.dragonet.proxy.network.session.ProxySession;
import org.dragonet.proxy.network.session.cache.object.CachedEntity;
import org.dragonet.proxy.network.translator.PacketTranslator;
import org.dragonet.proxy.network.translator.annotations.PCPacketTranslator;

@PCPacketTranslator(packetClass = ServerEntityHeadLookPacket.class)
public class PCEntityHeadlookTranslator extends PacketTranslator<ServerEntityHeadLookPacket> {
    public static final PCEntityHeadlookTranslator INSTANCE = new PCEntityHeadlookTranslator();

    @Override
    public void translate(ProxySession session, ServerEntityHeadLookPacket packet) {
        CachedEntity cachedEntity = session.getEntityCache().getByRemoteId(packet.getEntityId());
        if(cachedEntity == null) {
            //log.info("(debug) EntityHeadLook: Cached entity is null");
            return;
        }

        cachedEntity.setRotation(Vector3f.from(cachedEntity.getRotation().getX(), cachedEntity.getRotation().getY(), packet.getHeadYaw()));

        Vector3f rotation = Vector3f.from(cachedEntity.getRotation().getX() / (360d / 256d),
            cachedEntity.getRotation().getY() / (360d / 256d), cachedEntity.getRotation().getZ() / (360d / 256d));

        MoveEntityAbsolutePacket moveEntityPacket = new MoveEntityAbsolutePacket();
        moveEntityPacket.setRuntimeEntityId(cachedEntity.getProxyEid());
        moveEntityPacket.setPosition(cachedEntity.getOffsetPosition());
        moveEntityPacket.setRotation(rotation);
        moveEntityPacket.setOnGround(true);
        moveEntityPacket.setTeleported(false);

        session.sendPacket(moveEntityPacket);
    }
}
