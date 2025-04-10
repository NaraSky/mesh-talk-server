package com.lb.im.common.domain.model;

import java.io.Serializable;

/**
 * 基础消息
 */
public class TopicMessage implements Serializable {

    private static final long serialVersionUID = 5361804878646619571L;

    // 消息目的地，可以是消息的主题
    private String destination;

    public TopicMessage() {
    }

    public TopicMessage(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
