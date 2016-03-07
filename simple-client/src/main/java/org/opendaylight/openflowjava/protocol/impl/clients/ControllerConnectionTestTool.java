/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowjava.protocol.impl.clients;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.annotation.Arg;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.slf4j.LoggerFactory;

/**
 * ControllerConnectionTestTool class, utilities for testing device's connect
 * @author Jozef Bacigal
 * Date: 4.3.2016.
 */
public class ControllerConnectionTestTool {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ControllerConnectionTestTool.class);

    public static class Params {

        @Arg(dest = "controller-ip")
        public String controllerIP;

        @Arg(dest = "devices-count")
        public int deviceCount;

        @Arg(dest = "ssl")
        public boolean ssl;

        @Arg(dest = "threads")
        public int threads;

        @Arg(dest = "port")
        public int port;

        @Arg(dest = "timeout")
        public int timeout;

        @Arg(dest = "freeze")
        public int freeze;

        @Arg(dest = "sleep")
        public long sleep;

        static ArgumentParser getParser() throws UnknownHostException {
            final ArgumentParser parser = ArgumentParsers.newArgumentParser("openflowjava test-tool");

            parser.description("Openflowjava switch -> controller connector simulator");

            parser.addArgument("--device-count")
                    .type(Integer.class)
                    .setDefault(1)
                    .help("Number of simulated switches. Has to be more than 0")
                    .dest("devices-count");

            parser.addArgument("--controller-ip")
                    .type(String.class)
                    .setDefault("127.0.0.1")
                    .help("ODL controller ip address")
                    .dest("controller-ip");

            parser.addArgument("--ssl")
                    .type(Boolean.class)
                    .setDefault(false)
                    .help("Use secured connection")
                    .dest("ssl");

            parser.addArgument("--threads")
                    .type(Integer.class)
                    .setDefault(1)
                    .help("Number of threads: MAX 1024")
                    .dest("threads");

            parser.addArgument("--port")
                    .type(Integer.class)
                    .setDefault(6653)
                    .help("Connection port")
                    .dest("port");

            parser.addArgument("--timeout")
                    .type(Integer.class)
                    .setDefault(60)
                    .help("Timeout in seconds")
                    .dest("timeout");

            parser.addArgument("--scenarioTries")
                    .type(Integer.class)
                    .setDefault(3)
                    .help("Number of tries in scenario, while waiting for response")
                    .dest("freeze");

            parser.addArgument("--timeBetweenScenario")
                    .type(Long.class)
                    .setDefault(100)
                    .help("Waiting time in milliseconds between tries.")
                    .dest("sleep");

            return parser;
        }

        void validate() {
            checkArgument(deviceCount > 0, "Switch count has to be > 0");
            checkArgument(threads > 0 && threads < 1024, "Switch count has to be > 0 and < 1024");
        }
    }

    public static void main(final String[] args) {

        List<Callable<Boolean>> callableList = new ArrayList<>();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final Params params = parseArgs(args, Params.getParser());
            params.validate();

            for(int loop=0;loop < params.deviceCount; loop++){

                CallableClient cc = new CallableClient(
                        params.port,
                        params.ssl,
                        InetAddress.getByName(params.controllerIP),
                        "Switch no." + String.valueOf(loop),
                        new ScenarioHandler(ScenarioFactory.createHandshakeScenarioWithBarrier(), params.freeze, params.sleep),
                        new Bootstrap(),
                        workerGroup);

                callableList.add(cc);

            }

            ExecutorService executorService = Executors.newFixedThreadPool(params.threads);
            final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

            final List<ListenableFuture<Boolean>> listenableFutures = new ArrayList<>();
            for (Callable<Boolean> booleanCallable : callableList) {
               listenableFutures.add(listeningExecutorService.submit(booleanCallable));
            }
            final ListenableFuture<List<Boolean>> summaryFuture = Futures.successfulAsList(listenableFutures);
            List<Boolean> booleanList = summaryFuture.get(params.timeout, TimeUnit.SECONDS);
            Futures.addCallback(summaryFuture, new FutureCallback<List<Boolean>>() {
                @Override
                public void onSuccess(@Nullable final List<Boolean> booleanList) {
                    LOG.info("Tests finished");
                    workerGroup.shutdownGracefully();
                    LOG.info("Summary:");
                    int testsOK = 0;
                    int testFailure = 0;
                    for (Boolean aBoolean : booleanList) {
                        if (aBoolean) {
                            testsOK++;
                        } else {
                            testFailure++;
                        }
                    }
                    LOG.info("Tests OK: {}", testsOK);
                    LOG.info("Tests failure: {}", testFailure);
                    System.exit(0);
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    LOG.warn("Tests call failure");
                    workerGroup.shutdownGracefully();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            LOG.warn("Exception has been thrown: {}", e);
            System.exit(1);
        }
    }

    private static Params parseArgs(final String[] args, final ArgumentParser parser) throws ArgumentParserException {
        final Params opt = new Params();
        parser.parseArgs(args, opt);
        return opt;
    }


}