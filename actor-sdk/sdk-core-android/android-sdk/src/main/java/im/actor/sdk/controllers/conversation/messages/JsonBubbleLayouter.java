package im.actor.sdk.controllers.conversation.messages;

import org.jetbrains.annotations.NotNull;

import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.JsonContent;
import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;

public class JsonBubbleLayouter extends LambdaBubbleLayouter {


    public JsonBubbleLayouter(String dataType, @NotNull LambdaBubbleLayouter.ViewHolderCreator creator) {
        super(content ->
        {
            if (content instanceof JsonContent) {
                if (dataType == null) {
                    return true;
                }
                try {
                    return dataType.equals(new JSONObject(((JsonContent) content).getRawJson()).getString("dataType"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }, creator);
    }

    @Override
    public boolean isMatch(AbsContent content) {
        return matcher.isMatch(content);
    }

}
