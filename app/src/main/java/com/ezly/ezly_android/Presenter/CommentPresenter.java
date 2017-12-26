package com.ezly.ezly_android.Presenter;

import android.widget.Toast;

import com.ezly.ezly_android.Data.EzlySearchParam;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Utils.Helper.UIHelper.SingleToast;
import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Data.Response.GetEventCommentsResponse;
import com.ezly.ezly_android.Model.CommentModel;
import com.ezly.ezly_android.UI.ViewInterFace.CommentView;
import com.ezly.ezly_android.Utils.TextUtils;
import com.ezly.ezly_android.network.ServerHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Johnnie on 29/10/16.
 */

public class CommentPresenter extends BasePresenter {

    private CommentView mView;
    private List<EzlyUser> commentUsers;
    private List<EzlyComment> comments;

    private CommentModel commentModel;
    private MemberHelper memberHelper;

    @Inject
    public CommentPresenter(CommentModel commentModel, MemberHelper memberHelper) {
        this.commentModel = commentModel;
        this.memberHelper = memberHelper;

    }

    public void setView(CommentView view) {
        this.mView = view;
    }

    public void getComments(EzlyEvent event){
        commentModel.getComments(event)
                .map(new Func1<GetEventCommentsResponse, List<EzlyComment>>() {
                    @Override
                    public List<EzlyComment> call(GetEventCommentsResponse getEventCommentsResponse) {
                        comments = getEventCommentsResponse.getData().getComments();
                        commentUsers = getEventCommentsResponse.getData().getUsers();
                        return comments;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onCommentsPrepared(), onCommentsPreparedFailed());
    }

    private Action1<Throwable> onCommentsPreparedFailed() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if(mView != null){
                    String errorMsg = throwable.getMessage();
                    mView.onCommentsLoadFailed(errorMsg);
                }
            }
        };
    }

    public void postComments(EzlyEvent event, String commentText, String parentCommentID){
        HashMap<String, String> param = new HashMap<>();
        param.put("text", commentText);
        param.put("creationTime", getPostTime());
        param.put("byUserId", memberHelper.getCurrentUser().getId());
        if(!TextUtils.isEmpty(parentCommentID)){
            param.put("replyToId", parentCommentID);
        }

        commentModel.postComment(event, param)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onPostCommentComplete(), onRequestError());
    }

    public String getPostTime() {
        String result = "";
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        Date date = new Date();
        result = inFormat.format(date);
        return result;
    }

    public Action1<Response<Void>> onPostCommentComplete() {
        return new Action1<Response<Void>>() {
            @Override
            public void call(Response<Void> response) {
                if(mView != null){
                    boolean isSuccess = response.code() == 200 || response.code() == 201;
                    mView.onPostCommentComplete(isSuccess);
                }
            }
        };
    }

    protected Action1<Throwable> onRequestError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //TODO: handle error
                if(throwable != null && !TextUtils.isEmpty(throwable.getMessage())){
                    SingleToast.makeText(mView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT);
                }

            }
        };
    }

    public Action1<List<EzlyComment>> onCommentsPrepared() {
        return new Action1<List<EzlyComment>>() {
            @Override
            public void call(List<EzlyComment> comments) {
                mView.onCommentsPrepared(CommentPresenter.this.comments, CommentPresenter.this.commentUsers);
            }
        };
    }

    public EzlyUser getUserForComment(EzlyComment comment){
        EzlyUser user = null;
        for(EzlyUser u : commentUsers){
            if(u.getId().equals(comment.getUserId())){
                user = u;
                break;
            }
        }
        return user;
    }
}
