/*
 * Copyright (c) 2014 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.api.keys;

import org.junit.Assert;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.util.EncodeConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPhyPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.InPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.Nxm0Class;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.oxm.rev130731.OpenflowBasicClass;

/**
 * @author michal.polkorab
 *
 */
public class MatchEntrySerializerKeyTest {

    /**
     * Test MatchEntrySerializerKey equals and hashCode
     */
    @Test
    public void test() {
        MatchEntrySerializerKey<?, ?> key1 = new MatchEntrySerializerKey<>
                (EncodeConstants.OF13_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        MatchEntrySerializerKey<?, ?> key2 = new MatchEntrySerializerKey<>
                (EncodeConstants.OF13_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        Assert.assertTrue("Wrong equals", key1.equals(key2));
        Assert.assertTrue("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                OpenflowBasicClass.class, InPhyPort.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                Nxm0Class.class, InPort.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF10_VERSION_ID,
                OpenflowBasicClass.class, InPhyPort.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                OpenflowBasicClass.class, null);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID,
                null, InPhyPort.class);
        Assert.assertFalse("Wrong equals", key1.equals(key2));
        Assert.assertFalse("Wrong hashCode", key1.hashCode() == key2.hashCode());
    }
    
    /**
     * Test MatchEntrySerializerKey equals - additional test 
     */
    @Test
    public void testEquals(){
        
        MatchEntrySerializerKey<?, ?> key1;
        MatchEntrySerializerKey<?, ?> key2;
        
        key1 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        key2 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID, OpenflowBasicClass.class, InPort.class);
        
        Assert.assertTrue("Wrong equal to identical object.", key1.equals(key1));
        
        Assert.assertFalse("Wrong equal to different class.", key1.equals(new Object()));
        
        Long expId1 = 987654331L;
        Long expId2 = 123456789L;
        
        key1.setExperimenterId(null);
        key2.setExperimenterId(expId2);
        
        Assert.assertFalse("Wrong equal by experimenterId", key1.equals(key2));
        
        key1.setExperimenterId(expId1);
        Assert.assertFalse("Wrong equal by experimenterId", key1.equals(key2));
        
        key1 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID, null, InPort.class);
        Assert.assertFalse("Wrong equal by oxmClass", key1.equals(key2));
        
        key1 = new MatchEntrySerializerKey<>(EncodeConstants.OF13_VERSION_ID, OpenflowBasicClass.class, null);
        Assert.assertFalse("Wrong equal by oxmField", key1.equals(key2));
    }
}