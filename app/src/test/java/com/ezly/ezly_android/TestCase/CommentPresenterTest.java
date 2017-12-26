package com.ezly.ezly_android.TestCase;

import android.content.Context;

import com.ezly.ezly_android.Data.EzlyJob;
import com.ezly.ezly_android.Data.EzlyService;
import com.ezly.ezly_android.EzlyTestClasses.TestServerHelper;
import com.ezly.ezly_android.Model.CommentModel;
import com.ezly.ezly_android.Utils.Helper.MemberHelper;
import com.ezly.ezly_android.Data.EzlyComment;
import com.ezly.ezly_android.Data.EzlyEvent;
import com.ezly.ezly_android.Data.EzlyUser;
import com.ezly.ezly_android.Presenter.CommentPresenter;
import com.ezly.ezly_android.UI.ViewInterFace.CommentView;
import com.ezly.ezly_android.network.ServerHelper;
import com.mapbox.mapboxsdk.location.LocationListener;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class CommentPresenterTest extends BaseTest{

    @Inject Context context;
    @Inject CommentPresenter commentPresenter;
    @Inject MemberHelper memberHelper;
    @Inject ServerHelper serverHelper;

    @Before
    public void setUp() {
        getTestActivityComponent().inject(this);
        commentPresenter.setView(new CommentView() {
            @Override
            public void onCommentsPrepared(List<EzlyComment> comments, List<EzlyUser> users) {

            }

            @Override
            public void onCommentsLoadFailed(String errorMsg) {

            }

            @Override
            public void onPostCommentComplete(boolean isSuccess) {

            }

            @Override
            public Context getContext() {
                return context;
            }
        });
    }

    @After
    public void tearDown() {
        commentPresenter = null;
    }

    @Test
    public void testCommentPresenter_NotNull(){
        Assert.assertNotNull(commentPresenter);
    }

    @Test
    public void testGetUserForCommentForJob() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        commentPresenter.getComments(new EzlyJob());//dummy event to get test comments defined in TestServerHelper
        verify(serverHelper).getJobComments(ArgumentMatchers.<String>any());
        verify(commentPresenter).onCommentsPrepared();

        EzlyComment c2 = new EzlyComment();
        c2.setId("c2ID");
        c2.setText("c2 text");
        c2.setUserId("user 2");
        EzlyUser u2 = commentPresenter.getUserForComment(c2);

        assertEquals(u2.getId(), c2.getUserId());
    }

    @Test
    public void testGetUserForCommentForService() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        commentPresenter.getComments(new EzlyService());//dummy event to get test comments defined in TestServerHelper
        verify(serverHelper).getServiceComments(ArgumentMatchers.<String>any());
        verify(commentPresenter).onCommentsPrepared();

        EzlyComment c2 = new EzlyComment();
        c2.setId("c2ID");
        c2.setText("c2 text");
        c2.setUserId("user 2");
        EzlyUser u2 = commentPresenter.getUserForComment(c2);

        assertEquals(u2.getId(), c2.getUserId());
    }

    @Test
    public void testPostCommentForJob() throws InterruptedException {
        memberHelper.loginToEzly(context, "", "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        commentPresenter.postComments(new EzlyJob(), "", "");
        verify(serverHelper).postJobComments(ArgumentMatchers.<String>any(), ArgumentMatchers.<HashMap<String, String>>any());
        verify(commentPresenter).onPostCommentComplete();
    }

    @Test
    public void testPostCommentForService() throws InterruptedException {
        memberHelper.loginToEzly(context, "", "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        commentPresenter.postComments(new EzlyService(), "", "");
        verify(serverHelper).postServiceComments(ArgumentMatchers.<String>any(), ArgumentMatchers.<HashMap<String, String>>any());
        verify(commentPresenter).onPostCommentComplete();
    }
}
