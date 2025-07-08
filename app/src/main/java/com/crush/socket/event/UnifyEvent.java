package com.crush.socket.event;

/**
 * Created with IntelliJ IDEA.
 * User: Karl
 * Date: 2020/6/5
 * Time: 下午3:45
 */
public class UnifyEvent {

    /**
     * 事件类型
     */
    private String type;
    private String bizCode;
    private String head;
    private String body;

    public UnifyEvent() {
    }

    public UnifyEvent(String type, String body) {
        this.type = type;
        this.body = body;
    }

    public UnifyEvent(String type, String bizCode, String head, String body) {
        this.type = type;
        this.bizCode = bizCode;
        this.head = head;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
