package com.xfyyim.cn.bean;

import java.io.Serializable;

public class ZanEntivity implements Serializable {


    /**
     * faceIdentity : 0
     * msg : {"age":0,"body":{"images":[{"length":0,"oUrl":"http://file57.quyangapp.com/u/24/10000024/202005/o/544b94b865bd4cc99fd1991cfcac1ad0.jpg","size":0,"tUrl":"http://file57.quyangapp.com/u/24/10000024/202005/t/544b94b865bd4cc99fd1991cfcac1ad0.jpg"},{"length":0,"oUrl":"http://file57.quyangapp.com/u/24/10000024/202005/o/497ecedcbf734a46b26a84c7873ac5bf.jpg","size":0,"tUrl":"http://file57.quyangapp.com/u/24/10000024/202005/t/497ecedcbf734a46b26a84c7873ac5bf.jpg"}],"text":"","time":0,"type":2},"cityId":310100,"count":{"collect":0,"comment":1,"forward":0,"money":0,"play":0,"praise":1,"share":0,"total":2},"faceIdentity":0,"flag":3,"isAllowComment":1,"isCollect":0,"isPraise":0,"latitude":31.093847,"loc":{"lat":31.093847,"lng":121.521043},"longitude":121.521043,"model":"MI MAX 2","msgId":"5eba42f8a749861d647a2466","nickname":"舒克开飞机","parentTopicId":[{"parentTopicId":"5eb90b2a9eef536980216c3b"},{"parentTopicId":"5eb90b1b9eef536980216c3a"}],"parentTopicIds":[{"$ref":"$.data[0].msg.parentTopicId[0]"},{"$ref":"$.data[0].msg.parentTopicId[1]"}],"sex":0,"state":0,"time":1589265144,"topicStr":"#哇呜啊啊啊,#请问你是DJ吗，你猜呢？","topicType":1,"userId":10000024,"visible":1}
     * msgId : 5eba42f8a749861d647a2466
     * nickname : 舒克开飞机
     * praiseId : 5eba4fe3a749861d647a266d
     * time : 1589268451
     * toNickname : 舒克开飞机
     * toUserId : 10000024
     * userId : 10000024
     * vipFlag : 0
     */

    private int faceIdentity;
    private MsgBean msg;
    private String msgId;
    private String nickname;
    private String praiseId;
    private int time;
    private String toNickname;
    private int toUserId;
    private int userId;
    private int vipFlag;

    public int getFaceIdentity() {
        return faceIdentity;
    }

    public void setFaceIdentity(int faceIdentity) {
        this.faceIdentity = faceIdentity;
    }

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPraiseId() {
        return praiseId;
    }

    public void setPraiseId(String praiseId) {
        this.praiseId = praiseId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getToNickname() {
        return toNickname;
    }

    public void setToNickname(String toNickname) {
        this.toNickname = toNickname;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(int vipFlag) {
        this.vipFlag = vipFlag;
    }


}
