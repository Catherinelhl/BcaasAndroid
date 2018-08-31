package io.bcaas.bean;


import java.io.Serializable;

import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/16
 * <p>
 * 「語言切換」數據類
 */
public class LanguageSwitchingBean implements Serializable {

    private String type;
    private boolean isChoose;
    private String language;

    public LanguageSwitchingBean(String language, String type, boolean isChoose) {
        super();
        this.isChoose = isChoose;
        this.type = type;
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "LanguageSwitchingBean{" +
                "type='" + type + '\'' +
                ", isChoose=" + isChoose +
                ", language='" + language + '\'' +
                '}';
    }
}
