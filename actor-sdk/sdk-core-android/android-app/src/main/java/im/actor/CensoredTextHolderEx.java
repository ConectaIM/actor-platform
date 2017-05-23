package im.actor;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.sdk.controllers.conversation.messages.MessagesAdapter;
import im.actor.sdk.controllers.conversation.messages.content.TextHolder;

public class CensoredTextHolderEx extends TextHolder {
    public CensoredTextHolderEx(MessagesAdapter adapter, View itemView, Peer peer) {
        super(adapter, itemView, peer);
    }

    private static ArrayList<String> badWords;

    static {
        badWords = new ArrayList<>();
        badWords.add("fuck");
        badWords.add("poke");
        badWords.add("poké");
    }

    @Override
    public void bindRawText(CharSequence rawText, long readDate, long receiveDate, Spannable reactions, Message message, boolean isItalic) {
        Spannable res = new SpannableString(rawText);
        for (String s : badWords) {
//            rawText = rawText.toString().replaceAll("/*(?i)" + s + "/*", new String(new char[s.length()]).replace('\0', '*'));
            Pattern p = Pattern.compile("/*(?i)" + s + "/*");
            Matcher m = p.matcher(rawText.toString());
            while (m.find()) {
                CensorSpan span = new CensorSpan();
                res.setSpan(span, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }


        super.bindRawText(res, readDate, receiveDate, reactions, message, isItalic);
    }

    private class CensorSpan extends ClickableSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.BLACK);
            ds.bgColor = Color.BLACK;
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
