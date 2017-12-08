/*
 * Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.incubator.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.net.ssl.SSLEngine;

import jdk.incubator.http.internal.common.ByteBufferReference;
import jdk.incubator.http.internal.common.ExceptionallyCloseable;
import jdk.incubator.http.internal.common.Utils;

/**
 * Asynchronous version of SSLConnection.
 */
class AsyncSSLConnection extends HttpConnection
                         implements AsyncConnection, ExceptionallyCloseable {

    final AsyncSSLDelegate sslDelegate;
    final PlainHttpConnection plainConnection;

    AsyncSSLConnection(InetSocketAddress addr, HttpClientImpl client, String[] ap) {
        super(addr, client);
        plainConnection = new PlainHttpConnection(addr, client);
        sslDelegate = new AsyncSSLDelegate(plainConnection, client, ap);
    }

    @Override
    synchronized void configureMode(Mode mode) throws IOException {
        super.configureMode(mode);
        plainConnection.configureMode(mode);
    }

    private CompletableFuture<Void> configureModeAsync(Void ignore) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        try {
            configureMode(Mode.ASYNC);
            cf.complete(null);
        } catch (Throwable t) {
            cf.completeExceptionally(t);
        }
        return cf;
    }

    @Override
    public void connect() throws IOException, InterruptedException {
        plainConnection.connect();
        configureMode(Mode.ASYNC);
        startReading();
        sslDelegate.connect();
    }

    @Override
    public CompletableFuture<Void> connectAsync() {
        // not used currently
        throw new InternalError();
    }

    @Override
    boolean connected() {
        return plainConnection.connected() && sslDelegate.connected();
    }

    @Override
    boolean isSecure() {
        return true;
    }

    @Override
    boolean isProxied() {
        return false;
    }

    @Override
    SocketChannel channel() {
        return plainConnection.channel();
    }

    @Override
    public void enableCallback() {
        sslDelegate.enableCallback();
    }

    @Override
    ConnectionPool.CacheKey cacheKey() {
        return ConnectionPool.cacheKey(address, null);
    }

    @Override
    long write(ByteBuffer[] buffers, int start, int number)
        throws IOException
    {
        ByteBuffer[] bufs = Utils.reduce(buffers, start, number);
        long n = Utils.remaining(bufs);
        sslDelegate.writeAsync(ByteBufferReference.toReferences(bufs));
        sslDelegate.flushAsync();
        return n;
    }

    @Override
    long write(ByteBuffer buffer) throws IOException {
        long n = buffer.remaining();
        sslDelegate.writeAsync(ByteBufferReference.toReferences(buffer));
        sslDelegate.flushAsync();
        return n;
    }

    @Override
    public void writeAsyncUnordered(ByteBufferReference[] buffers) throws IOException {
        assert getMode() == Mode.ASYNC;
        sslDelegate.writeAsyncUnordered(buffers);
    }

    @Override
    public void writeAsync(ByteBufferReference[] buffers) throws IOException {
        assert getMode() == Mode.ASYNC;
        sslDelegate.writeAsync(buffers);
    }

    @Override
    public void flushAsync() throws IOException {
        sslDelegate.flushAsync();
    }

    @Override
    public void closeExceptionally(Throwable cause) {
        Utils.close(cause, sslDelegate, plainConnection.channel());
    }

    @Override
    public void close() {
        Utils.close(sslDelegate, plainConnection.channel());
    }

    @Override
    void shutdownInput() throws IOException {
        plainConnection.channel().shutdownInput();
    }

    @Override
    void shutdownOutput() throws IOException {
        plainConnection.channel().shutdownOutput();
    }

    SSLEngine getEngine() {
        return sslDelegate.getEngine();
    }

    @Override
    public void setAsyncCallbacks(Consumer<ByteBufferReference> asyncReceiver,
                                  Consumer<Throwable> errorReceiver,
                                  Supplier<ByteBufferReference> readBufferSupplier) {
        sslDelegate.setAsyncCallbacks(asyncReceiver, errorReceiver, readBufferSupplier);
        plainConnection.setAsyncCallbacks(sslDelegate::asyncReceive, errorReceiver, sslDelegate::getNetBuffer);
    }

    // Blocking read functions not used here

    @Override
    protected ByteBuffer readImpl() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    CompletableFuture<Void> whenReceivingResponse() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void startReading() {
        plainConnection.startReading();
        sslDelegate.startReading();
    }

    @Override
    public void stopAsyncReading() {
        plainConnection.stopAsyncReading();
    }
}
