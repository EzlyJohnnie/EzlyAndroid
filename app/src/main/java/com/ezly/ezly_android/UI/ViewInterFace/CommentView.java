package com.ezly.ezly_android.UI.ViewInterFace;

import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyUser;

import java.util.List;

/**
 * Created by Johnnie on 29/10/16.
 */

public interface CommentView extends BaseViewInterface {
    void onCommentsPrepared(List<EzlyComment> comments, List<EzlyUser> users);
    void onCommentsLoadFailed(String errorMsg);
    void onPostCommentComplete(boolean isSuccess);
}
