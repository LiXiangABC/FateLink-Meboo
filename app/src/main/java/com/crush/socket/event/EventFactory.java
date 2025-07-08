package com.crush.socket.event;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 把来自服务端的数据封装成事件，便于按顺序处理
 * User: Karl
 * Date: 2020/6/5
 * Time: 下午3:41
 */
public class EventFactory {

    private static ConcurrentLinkedQueue<UnifyEvent> quotesQueue = new ConcurrentLinkedQueue<>();

    public static ConcurrentLinkedQueue<UnifyEvent> getQuotesInstance() {
        return quotesQueue;
    }

}
