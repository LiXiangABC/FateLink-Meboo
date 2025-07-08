package com.crush.socket.netty;

import android.util.Log;

import com.custom.base.config.BaseConfig;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.rong.imkit.SpName;

/**
 * Created with IntelliJ IDEA.
 * User: Karl
 * Date: 2020/6/17
 * Time: 下午6:06
 */
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                if (NettyClient.getInst().getHandshaker() == null || !NettyClient.getInst().getHandshaker().isHandshakeComplete()) {
                    Log.i("NettyClient", "握手未成功,不做处理");
                } else {
                    Log.i("NettyClient", "服务端通讯异常,尝试重连");
                    ctx.channel().close();
                }
            } else if (event.state() == IdleState.WRITER_IDLE) {
                JSONObject initData = new JSONObject();
                initData.put("bizCode", "C10000");
                JSONObject head = new JSONObject();
                JSONObject body = new JSONObject();
                head.put("token",BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getUserCode(),""));
                body.put("userCode",BaseConfig.Companion.getGetInstance().getString(SpName.INSTANCE.getUserCode(),""));
                initData.put("head",head);
                initData.put("body",body);
                String s = initData.toString();
                Log.i("NettyClient", "发送心跳," + s);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(s));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
