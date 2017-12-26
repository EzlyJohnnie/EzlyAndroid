package com.ezly.ezly_android.Utils.Helper.UIHelper;

import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Johnnie on 21/06/17.
 */
public class JCSlidableCellRecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

    private SlidableCellTouchListenerDelegate delegate;

    private final static int MOVE_DIRECTION_VERTICAL = 0;
    private final static int MOVE_DIRECTION_HORIZONTAL = 1;

    private float x = 0;
    private float y = 0;
    private boolean isOpen;
    private int moveDirection = -1;

    private View childView;
    private View preContentView = null;
    private View contentView = null;
    private int position;
    private float slideMenuWidth;

    private boolean shouldConsumeTouchEvent;

    public JCSlidableCellRecyclerViewTouchListener(final SlidableCellTouchListenerDelegate delegate) {
        this.delegate = delegate;
        slideMenuWidth = delegate.getCellBottomMenuWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent motionEvent) {
        boolean consumeEvent = false;
        int action = motionEvent.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                shouldConsumeTouchEvent = false;
                moveDirection = -1;
                x = motionEvent.getRawX();
                y = motionEvent.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                handleMove(rv, motionEvent);
                break;

            case MotionEvent.ACTION_UP:
                endTouch();
                //if hasHandleMove, then return true to disable viewHolder onClickEvent
                consumeEvent = shouldConsumeTouchEvent;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                resetPreCellIfNeed();
                break;
        }

        childView = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if(childView == null){
            return false;
        }

        position = rv.getChildAdapterPosition(childView);
        contentView = childView.findViewById(delegate.getCellContentViewLayoutRes());
        if(contentView == null){
            return false;
        }

        return consumeEvent;
    }

    private void handleMove(RecyclerView rv, MotionEvent motionEvent) {
        float newX = motionEvent.getRawX();
        float newY = motionEvent.getRawY();
        if(x == 0){
            x = newX;
        }
        if(y == 0){
            y = newY;
        }

        float diffX = newX - x;
        float diffY = newY - y;

        //detect move direction
        if(moveDirection == -1 && (Math.abs(diffX) > 5 || Math.abs(diffY) > 5)){
            moveDirection = Math.abs(diffX) > Math.abs(diffY) ? MOVE_DIRECTION_HORIZONTAL : MOVE_DIRECTION_VERTICAL;

            //if horizontal move, disable clickEvent
            if(moveDirection == MOVE_DIRECTION_HORIZONTAL){
                shouldConsumeTouchEvent = true;
                if(contentView != null && contentView.getBackground() != null){
                    contentView.getBackground().setState(StateSet.WILD_CARD);
                }
            }
            else if(moveDirection == MOVE_DIRECTION_VERTICAL){
                resetPreCellIfNeed();
            }
        }

        //handel drag view
        if(shouldProcessEvent(rv, motionEvent)){
            if(preContentView != null && preContentView != contentView){
                resetPreCellIfNeed();
            }

            preContentView = contentView;

            if(moveDirection == MOVE_DIRECTION_HORIZONTAL && UIHelper.isPointInsideView(newX, motionEvent.getRawY(), contentView)){
                if (contentView.getX() + diffX < 0) {
                    if(Math.abs(contentView.getX() + diffX) > slideMenuWidth){
                        float decelerateRate = (contentView.getWidth()- slideMenuWidth - Math.abs(contentView.getX() + diffX)) / (contentView.getWidth() - slideMenuWidth / 2);
                        contentView.setX(contentView.getX() + diffX * decelerateRate);
                    }
                    else{
                        contentView.setX(contentView.getX() + diffX);
                    }

                }

                x = newX;
            }
        }
    }

    private boolean shouldProcessEvent(RecyclerView rv, MotionEvent motionEvent) {
        if(moveDirection != MOVE_DIRECTION_HORIZONTAL){
            return false;
        }

        if(motionEvent.getAction() != MotionEvent.ACTION_MOVE){
            return false;
        }

        if(!delegate.allowSlideCell(position)){
            return false;
        }

        childView = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if(childView == null){
            resetPreCellIfNeed();
            return false;
        }
        contentView = childView.findViewById(delegate.getCellContentViewLayoutRes());
        if(contentView == null){
            resetPreCellIfNeed();
            return false;
        }


        return true;
    }

    private void endTouch() {
        if(contentView != null){
            if(isOpen){
                isOpen = Math.abs(contentView.getX()) >= slideMenuWidth * 0.75;
            }
            else{
                isOpen = Math.abs(contentView.getX()) > slideMenuWidth * 0.25;
            }
            animateContentView(contentView, isOpen);
        }
    }

    public void resetPreCellIfNeed(){
        if(preContentView != null){
            animateContentView(preContentView, false);
        }
    }

    private void animateContentView(View contentView, boolean toOpen){
        final SpringAnimation springAnim = new SpringAnimation(contentView, DynamicAnimation.TRANSLATION_X, toOpen ? -slideMenuWidth : 0);
        springAnim.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);
        springAnim.start();
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent motionEvent) {}
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    public void purge() {
        delegate = null;

        childView = null;
        preContentView = null;
        contentView = null;
    }

    public interface SlidableCellTouchListenerDelegate {
        int getCellContentViewLayoutRes();
        int getCellBottomMenuWidth();
        boolean allowSlideCell(int position);
    }
}
