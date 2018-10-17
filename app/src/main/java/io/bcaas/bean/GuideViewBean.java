package io.bcaas.bean;

import java.io.Serializable;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/17
 * <p>
 * 用于引导页面
 */
public class GuideViewBean implements Serializable {
    private int background;
    private String content;
    private String tag;
    private String buttonContent;

    public GuideViewBean() {
        super();
    }

    public GuideViewBean(int background, String content, String tag, String buttonContent) {
        super();
        this.background = background;
        this.content = content;
        this.tag = tag;
        this.buttonContent = buttonContent;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getButtonContent() {
        return buttonContent;
    }

    public void setButtonContent(String buttonContent) {
        this.buttonContent = buttonContent;
    }

    @Override
    public String toString() {
        return "GuideViewBean{" +
                "background=" + background +
                ", content='" + content + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
