package com.xfyyim.cn.call;

import com.xfyyim.cn.bean.message.ChatMessage;

/**
 * Created by Administrator on 2017/6/26 0026.
 */
public class MessageEventSipEVent {
    public final String touserid;
    public ChatMessage message;

    public MessageEventSipEVent(String touserid, ChatMessage message) {
        this.touserid = touserid;
        this.message = message;
    }
}