/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */

package org.opendaylight.openflowjava.protocol.impl.clients;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.opendaylight.openflowjava.protocol.impl.util.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.SettableFuture;

/**
 *
 * @author michal.polkorab
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClientHandler.class);
    private SettableFuture<Boolean> isOnlineFuture;
    private SettableFuture<Void> dataReceived;
    private int dataLimit;
    private int dataCounter = 0;

    /**
     * @param isOnlineFuture future notifier of connected channel
     */
    public SimpleClientHandler(SettableFuture<Boolean> isOnlineFuture) {
        this.isOnlineFuture = isOnlineFuture;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler - start of read");
        ByteBuf bb = (ByteBuf) msg;
        dataCounter += bb.readableBytes();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ByteBufUtils.byteBufToHexString(bb));
        }
        LOGGER.info(msg.toString());
        LOGGER.info("SimpleClientHandler - end of read");
        if (dataCounter >= dataLimit) {
            LOGGER.debug("data obtained");
            dataReceived.set(null);
        }
    }
    
/* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("CLIENT IS ACTIVE");
        if (isOnlineFuture != null) {
            isOnlineFuture.set(true);
            isOnlineFuture = null;
        }
    }

    /**
     * @param dataReceived
     * @param dataLimit
     */
    public void setDataReceivedFuture(SettableFuture<Void> dataReceived, int dataLimit) {
        this.dataReceived = dataReceived;
        this.dataLimit = dataLimit;
    }
    
    
}
