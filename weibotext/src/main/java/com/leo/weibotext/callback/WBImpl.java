package com.leo.weibotext.callback;

public interface WBImpl {
    WBImpl DEFAULT = new WBImplAdapter();

    class WBImplAdapter implements WBImpl {
        @Override
        public String getAllRegular() {
            return "(@[\u4e00-\u9fa5\\w.-。【】『』]+\\s)|(#[^#]+#)|((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
        }

        @Override
        public String getCallRegular() {
            return "@[\u4e00-\u9fa5\\w.-。【】『』]+\\s";
        }

        @Override
        public String getTopicRegular() {
            return "#[^#]+#";
        }

        @Override
        public String getHtmlRegular() {
            return "(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        }

        @Override
        public String getCallAndTopicRegular() {
            return "(@[\u4e00-\u9fa5\\w.-。【】『』]+\\s)|(#[^#]+#)";
        }

        @Override
        public String getCallAndHtmlRegular() {
            return "(@[\u4e00-\u9fa5\\w.-。【】『』]+\\s)|((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
        }

        @Override
        public String getTopicAndHtmlRegular() {
            return "(#[^#]+#)|((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
        }
    }

    String getAllRegular();

    String getCallRegular();

    String getTopicRegular();

    String getHtmlRegular();

    String getCallAndTopicRegular();

    String getCallAndHtmlRegular();

    String getTopicAndHtmlRegular();
}
