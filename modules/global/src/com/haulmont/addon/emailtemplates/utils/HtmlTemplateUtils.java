package com.haulmont.addon.emailtemplates.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlTemplateUtils {

    public static String prettyPrintHTML(String rawHTML) {
        Document doc = Jsoup.parseBodyFragment(rawHTML);
        return doc.body().html();
    }

}
