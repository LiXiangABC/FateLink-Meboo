package com.crush.socket.netty;

import android.content.Context;
import android.util.Log;

import com.crush.util.CollectionUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {
    private static final String TAG = "NettyClient";
    private static NettyClient singleInstance = new NettyClient();
    private List<String> currentList = new ArrayList<>();
    private Context context;
    public static NettyClient getInst() {
        return singleInstance;
    }

    private Channel channel;

    private boolean connectFlag = false;

    private String token;

    public void setContext(Context context){
        this.context = context;
    }
    /**
     * 服务器ip地址
     */
    private String hostUrl;

    private EventLoopGroup eventLoopGroup;

    private WebSocketClientHandshaker handshaker;

    public boolean isConnectFlag() {
        return connectFlag;
    }

    public NettyClient setConnectFlag(boolean connectFlag) {
        this.connectFlag = connectFlag;
        return this;
    }

    public void setCurrentList(List<String> data) {
        this.currentList = data;
    }

    public String getToken() {
        return token;
    }


    public NettyClient setToken(String token) {
        this.token = token;
        return this;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public NettyClient setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
        return this;
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public NettyClient() {
    }

    public void reset() {
        this.handshaker = null;
        this.channel = null;
        this.connectFlag = false;
    }
    int retryCount = 0; // 定义重新连接计数器

    /**
     * 开始连接服务器
     *
     */
    public void startConnectServerAlways() {
        while (true) {
            while (!this.connectFlag && retryCount < 3) {
                //输入用户名，然后登录
                startConnectServer();
                try {
                    Thread.sleep(100);
                    Log.i(TAG, "触发重新连接1...");
                    retryCount++; // 每次连接失败后增加计数器
                } catch (InterruptedException e) {
                    break; // 发生中断时退出循环
                }
                if (retryCount >= 3) {
                    // 如果重新连接3次后仍然未连接成功，则退出外部循环
                    Log.i(TAG, "已达到最大重新连接次数，退出连接循环。");
                    break;
                }
            }
        }
    }

    /**
     * 异常重连
     */
    private void startConnectServer() {
        //登录
        if (connectFlag) {
            Log.i(TAG, "已经登录成功，不需要重复登录");
            return;
        }
        Log.i(TAG, "开始连接Netty服务节点");
        if (context!= null){
//            AppsFlyerLib.getInstance().trackEvent(context, BuriedPointConfig.Socket_Begin_to_connect, null);
        }
        this.reset();
        this.doConnect();
    }

    /**
     * 重连
     */
    private void doConnect() {
        try {
            URI uri = null;
            Log.e(TAG, "doConnect: "+this.hostUrl );
            try {
                uri = new URI(this.hostUrl);
            } catch (URISyntaxException e) {
            }

            String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
            final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
            final int port;
            if (uri.getPort() == -1) {
                if ("ws".equalsIgnoreCase(scheme)) {
                    port = 80;
                } else if ("wss".equalsIgnoreCase(scheme)) {
                    port = 443;
                } else {
                    port = -1;
                }
            } else {
                port = uri.getPort();
            }

            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                Log.i(TAG, "Only WS(S) is supported.");
                return;
            }

            final boolean ssl = "wss".equalsIgnoreCase(scheme);
            final SslContext sslCtx;
            if (ssl) {
                try {
                    sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } catch (SSLException e) {
                    Log.i(TAG, "ssl build error", e);
                    return;
                }
            } else {
                sslCtx = null;
            }

            this.eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(this.eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    if (sslCtx != null) {
                        p.addLast(sslCtx.newHandler(socketChannel.alloc(), host, port));
                    }
                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpObjectAggregator(1024 * 1024 * 10));
                    p.addLast(new IdleStateHandler(8, 5, 0, TimeUnit.SECONDS));
                    p.addLast(new HeartBeatClientHandler());
                    p.addLast("hookedHandler", new WebSocketClientHandler());
                }
            });

            ChannelFuture future = null;
            //防止初次链接不上 每隔十秒重连一次
            while (future == null || !future.isSuccess()) {
                try {
                    future = bootstrap.connect(uri.getHost(), port).addListener((ChannelFuture f) -> {
                                if (!f.isSuccess()) {
                                    Log.i(TAG, "服务器连接失败!");
                                    connectFlag = false;
                                    if (context!= null) {
//                                        AppsFlyerLib.getInstance().trackEvent(context, BuriedPointConfig.Socket_Server_Connection_Failed, null);
                                    }
                                } else {
                                    connectFlag = true;
                                    Log.i(TAG, "服务器连接成功!");
                                    channel = f.channel();
                                    if (context!= null) {
//                                        AppsFlyerLib.getInstance().trackEvent(context, BuriedPointConfig.Socket_Server_Connection_Success, null);
                                    }
                                }
                            }
                    ).sync();
                } catch (Exception ex) {
                    Log.i(TAG, "重连中,host:" + host + ",port:" + port + " ......");
                    Thread.sleep(1000);

                }
            }

            //进行握手
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                    uri, WebSocketVersion.V13, (String) null, true, new DefaultHttpHeaders());
            Log.i(TAG, "准备握手...");

            WebSocketClientHandler handler = (WebSocketClientHandler) future.channel().pipeline().get("hookedHandler");
            handler.setHandshaker(handshaker);
            handshaker.handshake(future.channel());
            //阻塞等待是否握手成功 通过5秒轮训来避免无限等待
//            handler.handshakeFuture().sync();
            int i = 0;
            while (!handshaker.isHandshakeComplete()) {
                if (i % 10 == 0) {
                    Log.i(TAG, "握手中...");
                }
                if (i > 50) {
                    throw new RuntimeException("Handshake timeout!");
                }
                Thread.sleep(100);
                i++;
            }

            this.handshaker = handshaker;
            Log.i(TAG, "握手成功");

            //上报token
//            changeSubscription(currentList);
            if (context!= null) {
//                AppsFlyerLib.getInstance().trackEvent(context, BuriedPointConfig.Socket_Connect_Success, null);
            }
            future.channel().closeFuture().sync();
        } catch (Exception e) {
        } finally {
            this.close();
            if (context!= null){
//                AppsFlyerLib.getInstance().trackEvent(context, BuriedPointConfig.Socket_Reconnect_the, null);
            }
        }
    }

    /**
     * 修改订阅
     *
     * @param list
     */
    public void changeSubscription(List<String> list) {
        if (this.channel == null) {
            return;
        }
        if (!CollectionUtils.isEmpty(list)) {
            currentList = list;
        }
        try {
            //上报token
            JSONObject initData = new JSONObject();
            JSONArray subscription = new JSONArray();
            for (String s : list) {
                subscription.put(s);
            }

            initData.put("bizCode", "C0001");
            JsonObject jsonObject = new JsonObject();
//            String deviceId = CommonUtils.getDeviceId(AbcOptionApplication.getInstance());
//            jsonObject.addProperty("deviceId", deviceId);
            jsonObject.addProperty("token", this.token);
            initData.put("head", jsonObject.toString());
            Log.e(TAG, "changeSubscription: " + jsonObject.toString());
            JSONObject body = new JSONObject();
            body.put("subscription", subscription);
            initData.put("body", body.toString());
            this.channel.writeAndFlush(new TextWebSocketFrame(initData.toString()));
            Log.i(TAG, "修改渠道," + list.toString());

        } catch (Exception e) {
        }
    }

    public void close() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        this.handshaker = null;
        this.channel = null;
        this.connectFlag = false;
    }
}
