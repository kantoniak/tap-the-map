package com.kantoniak.discrete_fox.scene;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.rajawali3d.math.vector.Vector3;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class GameSurfaceOnTouchLister implements View.OnTouchListener {

    class GestureTap extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mRenderer.onTouchEvent(e);
            return true;
        }
    }

    private final MapRenderer mRenderer;

    private int mActivePointerId = INVALID_POINTER_ID;
    private GestureDetector mTapDetector;

    private Vector3 worldOffset = MapRenderer.MAP_MIDDLE.clone();

    private float mLastTouchX;
    private float mLastTouchY;

    public GameSurfaceOnTouchLister(Context context, MapRenderer renderer) {
        this.mRenderer = renderer;
        this.mTapDetector = new GestureDetector(context, new GestureTap());
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {

        if (mTapDetector.onTouchEvent(e)) {
            return true;
        }

        final int action = e.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = e.getActionIndex();

                mLastTouchX = e.getX(pointerIndex);
                mLastTouchY = e.getY(pointerIndex);

                mActivePointerId = e.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = e.findPointerIndex(mActivePointerId);

                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                worldOffset.add(new Vector3(dx, 0, dy).multiply(0.001f));
                mRenderer.setWorldOffset(worldOffset);

                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = e.getActionIndex();
                final int pointerId = e.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = e.getX(newPointerIndex);
                    mLastTouchY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;

    }
}
