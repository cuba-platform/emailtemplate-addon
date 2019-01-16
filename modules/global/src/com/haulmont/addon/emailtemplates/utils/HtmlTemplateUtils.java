package com.haulmont.addon.emailtemplates.utils;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class HtmlTemplateUtils {

    public static String prettyPrintHTML(String rawHTML) {
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setIndentContent(true);
        tidy.setPrintBodyOnly(true);
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setSmartIndent(true);
        tidy.setShowWarnings(false); //to hide errors
        tidy.setQuiet(true); //to hide warning
        tidy.setTidyMark(false);

        // HTML to DOM
        Document htmlDOM = tidy.parseDOM(new ByteArrayInputStream(rawHTML.getBytes()), null);

        // Pretty Print
        OutputStream out = new ByteArrayOutputStream();
        tidy.pprint(htmlDOM, out);

        return out.toString();
    }

}
