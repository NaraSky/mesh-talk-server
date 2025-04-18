package com.lb.im.server.application.consumer;

import com.alibaba.fastjson.JSONObject;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.model.IMReceiveInfo;

/**
 * BaseMessageConsumer类作为消息消费者的基类，负责解析接收到的消息字符串并转换为IMReceiveInfo对象。
 */
public class BaseMessageConsumer {

    /**
     * 解析接收到的消息字符串，提取事件内容并转换为IMReceiveInfo对象。
     *
     * @param msg 接收到的原始消息字符串
     * @return 解析后的IMReceiveInfo对象
     */
    protected IMReceiveInfo getReceiveMessage(String msg) {
        // 将原始消息字符串解析为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(msg);
        // 根据预定义的常量键从JSON对象中获取事件内容字符串
        String eventStr = jsonObject.getString(IMConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, IMReceiveInfo.class);
    }

}
