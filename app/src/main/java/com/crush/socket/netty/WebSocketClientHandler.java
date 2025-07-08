package com.crush.socket.netty;

import android.util.Log;


import com.crush.socket.event.EventFactory;
import com.crush.socket.event.UnifyEvent;
import com.custom.base.config.BaseConfig;

import org.json.JSONException;
import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.CharsetUtil;
import io.rong.imkit.SpName;

/**
 * Created with IntelliJ IDEA.
 * User: Karl
 * Date: 2020/6/15
 * Time: 下午3:19
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final String TAG = "NettyClient";
    WebSocketClientHandshaker handshaker;
    ChannelPromise handshakeFuture;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    /**
     * 客户端连接服务器后被调用
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.i(TAG, "连接建立成功");
    }

    /**
     * 服务端端终止连接 后触发此函数
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.i(TAG, "服务端终止了服务");
    }

    /**
     * 发生异常时被调用
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //捕捉异常信息
        Log.i(TAG, "服务端发生异常【" + cause.getMessage() + "】", cause);
        ctx.close();
    }

    /**
     * 接受到数据信息
     *
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.i(TAG, "channelRead0  " + this.handshaker.isHandshakeComplete());
        Channel ch = ctx.channel();
        FullHttpResponse response;
        if (!this.handshaker.isHandshakeComplete()) {
            try {
                response = (FullHttpResponse) msg;
                //握手协议返回，设置结束握手
                this.handshaker.finishHandshake(ch, response);
                //设置成功
                this.handshakeFuture.setSuccess();
                Log.i(TAG, "WebSocket Client connected! response headers[sec-websocket-extensions]:{}" + response.headers());
            } catch (WebSocketHandshakeException var7) {
                FullHttpResponse res = (FullHttpResponse) msg;
                String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        } else if (msg instanceof FullHttpResponse) {
            response = (FullHttpResponse) msg;
            //this.listener.onFail(response.status().code(), response.content().toString(CharsetUtil.UTF_8));
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        } else {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                String transDataStr = textFrame.text();
                Log.i(TAG, "TextWebSocketFrame," + transDataStr);

                JSONObject jsonObject = new JSONObject(transDataStr);
                if (jsonObject.isNull("code")) {
                    String bizCode = jsonObject.optString("bizCode");
                    String head = jsonObject.optString("head");
                    String body = jsonObject.optString("body");
                    EventFactory.getQuotesInstance().add(new UnifyEvent("text", bizCode, head, body));
                    sendMessage(ctx, body);
                }
            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
                Log.i(TAG, "BinaryWebSocketFrame");
            } else if (frame instanceof PongWebSocketFrame) {
                Log.i(TAG, "WebSocket Client received pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                Log.i(TAG, "receive close frame");
                ch.close();
            }

        }
    }

    private void sendMessage(ChannelHandlerContext ctx, String body){
        JSONObject initData = new JSONObject();
        try {
            initData.put("bizCode", "C00000");
            JSONObject head = new JSONObject();
            head.put("token", BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getUserCode(),""));
            initData.put("head",head);
            initData.put("body",body);
            String s = initData.toString();
            Log.i("NettyClient", "发送心跳," + s);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(s));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}