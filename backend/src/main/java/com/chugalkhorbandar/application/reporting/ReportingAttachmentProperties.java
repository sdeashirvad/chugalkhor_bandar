package com.chugalkhorbandar.application.reporting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.reporting.attachments")
public class ReportingAttachmentProperties {

    private boolean txt = true;
    private boolean json = true;
    private boolean md = true;
    private boolean html = false;

    public boolean isTxt() {
        return txt;
    }

    public void setTxt(boolean txt) {
        this.txt = txt;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public boolean isMd() {
        return md;
    }

    public void setMd(boolean md) {
        this.md = md;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
