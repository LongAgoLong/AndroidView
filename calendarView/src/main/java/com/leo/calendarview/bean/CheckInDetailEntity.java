package com.leo.calendarview.bean;

import java.util.List;

/**
 * Created by LEO
 * on 2017/5/12.
 * 签到详情解析类
 */
public class CheckInDetailEntity {

    private int result;
    private int is_check;
    private int is_reward;
    private int gift_result;
    private int ad_result;
    private CheckInfoBean check_info;
    private List<CheckLogBean> check_log;
    private List<GiftDataBean> gift_data;
    private List<AdDataBean> ad_data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getIs_check() {
        return is_check;
    }

    public void setIs_check(int is_check) {
        this.is_check = is_check;
    }

    public int getIs_reward() {
        return is_reward;
    }

    public void setIs_reward(int is_reward) {
        this.is_reward = is_reward;
    }

    public int getGift_result() {
        return gift_result;
    }

    public void setGift_result(int gift_result) {
        this.gift_result = gift_result;
    }

    public int getAd_result() {
        return ad_result;
    }

    public void setAd_result(int ad_result) {
        this.ad_result = ad_result;
    }

    public CheckInfoBean getCheck_info() {
        return check_info;
    }

    public void setCheck_info(CheckInfoBean check_info) {
        this.check_info = check_info;
    }

    public List<CheckLogBean> getCheck_log() {
        return check_log;
    }

    public void setCheck_log(List<CheckLogBean> check_log) {
        this.check_log = check_log;
    }

    public List<GiftDataBean> getGift_data() {
        return gift_data;
    }

    public void setGift_data(List<GiftDataBean> gift_data) {
        this.gift_data = gift_data;
    }

    public List<AdDataBean> getAd_data() {
        return ad_data;
    }

    public void setAd_data(List<AdDataBean> ad_data) {
        this.ad_data = ad_data;
    }

    public static class CheckInfoBean {
        /**
         * con_num : 5
         * month_num : 2
         * reply_card : 0
         * buy_coins : 100
         * check_intro : 1.每日签到可获得10喵粮奖励，连续签到7天后追加30喵粮，第8天10喵粮，连续7天后追加30喵粮，依此反复循环&br2.每月满签到，可获得随机刮奖机会一次，奖品是每个月都不样的实物奖品&br3.中断的日期可以使用补签卡进行补签；补签卡可购买，100喵粮/张；也可在扭蛋抽奖中中奖获得,补签卡有效期15天&br4.普通用户每月拥有两次补签机会&br5.VIP用户每月拥有5次补签机会，且购买前两张补签卡免费&br6.在用户排行榜中连续签到最多的前20名用户，年终会得到喵特的特殊奖励哦&br#PS：VIP大老爷们抽中实物奖励的机会更大哟#
         * reply_intro : 每月只能抽奖一次，请选择想要的奖品抽奖吧
         */

        private int con_num;
        private int month_num;
        private int reply_card;
        private int buy_coins;
        private String check_intro;
        private String reply_intro;
        private int lottery_num;//满足抽奖天数

        public int getLottery_num() {
            return lottery_num;
        }

        public void setLottery_num(int lottery_num) {
            this.lottery_num = lottery_num;
        }

        public int getCon_num() {
            return con_num;
        }

        public void setCon_num(int con_num) {
            this.con_num = con_num;
        }

        public int getMonth_num() {
            return month_num;
        }

        public void setMonth_num(int month_num) {
            this.month_num = month_num;
        }

        public int getReply_card() {
            return reply_card;
        }

        public void setReply_card(int reply_card) {
            this.reply_card = reply_card;
        }

        public int getBuy_coins() {
            return buy_coins;
        }

        public void setBuy_coins(int buy_coins) {
            this.buy_coins = buy_coins;
        }

        public String getCheck_intro() {
            return check_intro;
        }

        public void setCheck_intro(String check_intro) {
            this.check_intro = check_intro;
        }

        public String getReply_intro() {
            return reply_intro;
        }

        public void setReply_intro(String reply_intro) {
            this.reply_intro = reply_intro;
        }

    }

    public static class CheckLogBean {
        /**
         * ctime : 1494385667
         * is_reply : 0
         */

        private String ctime;
        private String is_reply;
        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getIs_reply() {
            return is_reply;
        }

        public void setIs_reply(String is_reply) {
            this.is_reply = is_reply;
        }
    }

    public static class GiftDataBean {
        /**
         * is_check : 0
         * id : 100030
         * name : 乌龙院茶叶罐
         * img : /data/upload/2017/0420/17/58f87f2ca1172.jpg
         * reward_num : 0
         * reward_url : https://www.nyato.com/topic?id=100030
         */

        private String is_check;
        private String id;
        private String name;
        private String img;
        private String reward_num;
        private String reward_url;

        public String getIs_check() {
            return is_check;
        }

        public void setIs_check(String is_check) {
            this.is_check = is_check;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getReward_num() {
            return reward_num;
        }

        public void setReward_num(String reward_num) {
            this.reward_num = reward_num;
        }

        public String getReward_url() {
            return reward_url;
        }

        public void setReward_url(String reward_url) {
            this.reward_url = reward_url;
        }
    }

    public static class AdDataBean {
        /**
         * id : 52
         * uid : 10006
         * type : 7
         * url : https://www.nyato.com/
         * title : 签到
         * logo : /data/upload/2017/0509/15/59116bfb93323.jpg
         * cTime : 1494313979
         * exhibition_id : 0
         */

        private String id;
        private String uid;
        private String type;
        private String url;
        private String title;
        private String logo;
        private String cTime;
        private String new_url;
        private String exhibition_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getCTime() {
            return cTime;
        }

        public void setCTime(String cTime) {
            this.cTime = cTime;
        }

        public String getExhibition_id() {
            return exhibition_id;
        }

        public void setExhibition_id(String exhibition_id) {
            this.exhibition_id = exhibition_id;
        }

        public String getNew_url() {
            return new_url;
        }

        public void setNew_url(String new_url) {
            this.new_url = new_url;
        }
    }
}
