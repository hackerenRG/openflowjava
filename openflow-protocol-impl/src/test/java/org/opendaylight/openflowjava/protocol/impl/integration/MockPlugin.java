/* Copyright (C)2013 Pantheon Technologies, s.r.o. All rights reserved. */
package org.opendaylight.openflowjava.protocol.impl.integration;

import java.net.InetAddress;

import org.opendaylight.openflowjava.protocol.api.connection.ConnectionAdapter;
import org.opendaylight.openflowjava.protocol.api.connection.SwitchConnectionHandler;
import org.opendaylight.openflowjava.protocol.impl.util.BufferHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.EchoRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ErrorMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.FlowRemovedMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.HelloMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartReplyMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.MultipartRequestMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OpenflowProtocolListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PacketInMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.PortStatusMessage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michal.polkorab
 *
 */
public class MockPlugin implements OpenflowProtocolListener, SwitchConnectionHandler, SystemNotificationsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockPlugin.class);
    private ConnectionAdapter adapter;
    
    
    @Override
    public void onSwitchConnected(ConnectionAdapter connection) {
        LOGGER.debug("onSwitchConnected");
        this.adapter = connection;
        connection.setMessageListener(this);
        connection.setSystemListener(this);
    }

    @Override
    public boolean accept(InetAddress switchAddress) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onEchoRequestMessage(EchoRequestMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onErrorMessage(ErrorMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onExperimenterMessage(ExperimenterMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFlowRemovedMessage(FlowRemovedMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHelloMessage(HelloMessage notification) {
        LOGGER.debug("Hello message received");
        HelloInputBuilder hib = new HelloInputBuilder();
        try {
            BufferHelper.setupHeader(hib);
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }
        HelloInput hi = hib.build();
        adapter.hello(hi);
    }

    @Override
    public void onMultipartReplyMessage(MultipartReplyMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMultipartRequestMessage(MultipartRequestMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPacketInMessage(PacketInMessage notification) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPortStatusMessage(PortStatusMessage notification) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.SystemNotificationsListener#onDisconnectEvent(org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.system.rev130927.DisconnectEvent)
     */
    @Override
    public void onDisconnectEvent(DisconnectEvent notification) {
        LOGGER.debug("disconnection ocured: "+notification.getInfo());
    }

}
