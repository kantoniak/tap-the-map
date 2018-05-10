package pl.gov.stat.tapthemap.scene;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.rajawali3d.math.vector.Vector2;
import org.rajawali3d.math.vector.Vector3;

import pl.gov.stat.tapthemap.ar.ViewMatrixOverrideCamera;

public class GameSurfaceOnTouchLister implements View.OnTouchListener {

    private class PointerData {
        final int pointerId;
        float lastTouchX;
        float lastTouchY;

        public PointerData(int pointerId) {
            this.pointerId = pointerId;
        }
    }

    private class GestureTap extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mRenderer.onTouchEvent(e);
            return true;
        }
    }

    private final MapRenderer mRenderer;
    private final ViewMatrixOverrideCamera mCamera;

    private final GestureDetector mTapDetector;
    private @Nullable
    PointerData primaryPointer;
    private @Nullable
    PointerData secondaryPointer;
    private @Nullable
    Vector2 touchFocalPoint;
    private double initialScaleDistance;
    private float initialZoom;

    public GameSurfaceOnTouchLister(Context context, MapRenderer renderer, ViewMatrixOverrideCamera camera) {
        this.mRenderer = renderer;
        this.mCamera = camera;
        this.mTapDetector = new GestureDetector(context, new GestureTap());
        this.primaryPointer = null;
        this.secondaryPointer = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {

        // Country tapping
        if (mTapDetector.onTouchEvent(e)) {
            return true;
        }

        // Dragging & scale
        final int action = e.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (primaryPointer == null) {
                    primaryPointer = new PointerData(e.getPointerId(e.getActionIndex()));

                    final int pointerIndex = e.getActionIndex();
                    primaryPointer.lastTouchX = e.getX(pointerIndex);
                    primaryPointer.lastTouchY = e.getY(pointerIndex);
                    touchFocalPoint = new Vector2(primaryPointer.lastTouchX, primaryPointer.lastTouchY);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                if (secondaryPointer == null) {
                    final int primaryPointerIndex = e.findPointerIndex(primaryPointer.pointerId);
                    primaryPointer.lastTouchX = e.getX(primaryPointerIndex);
                    primaryPointer.lastTouchY = e.getY(primaryPointerIndex);

                    final int pointerIndex = e.getActionIndex();
                    secondaryPointer = new PointerData(e.getPointerId(pointerIndex));
                    secondaryPointer.lastTouchX = e.getX(pointerIndex);
                    secondaryPointer.lastTouchY = e.getY(pointerIndex);

                    touchFocalPoint = new Vector2(
                            (primaryPointer.lastTouchX + secondaryPointer.lastTouchX) * 0.5f,
                            (primaryPointer.lastTouchY + secondaryPointer.lastTouchY) * 0.5f
                    );

                    initialScaleDistance = calculatePointersDistance();
                    initialZoom = mCamera.getZoom();
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                if (secondaryPointer != null) {
                    final int primaryPointerIndex = e.findPointerIndex(primaryPointer.pointerId);
                    final int secondaryPointerIndex = e.findPointerIndex(secondaryPointer.pointerId);

                    primaryPointer.lastTouchX = e.getX(primaryPointerIndex);
                    primaryPointer.lastTouchY = e.getY(primaryPointerIndex);
                    secondaryPointer.lastTouchX = e.getX(secondaryPointerIndex);
                    secondaryPointer.lastTouchY = e.getY(secondaryPointerIndex);

                    // Dragging
                    Vector2 newTouchFocalPoint = new Vector2(
                            (primaryPointer.lastTouchX + secondaryPointer.lastTouchX) * 0.5f,
                            (primaryPointer.lastTouchY + secondaryPointer.lastTouchY) * 0.5f
                    );

                    final double dx = newTouchFocalPoint.getX() - touchFocalPoint.getX();
                    final double dy = newTouchFocalPoint.getY() - touchFocalPoint.getY();

                    final double cameraCoefficient = 0.0005d / mCamera.getZoom();
                    final Vector3 delta = new Vector3(dx, 0, dy).multiply(cameraCoefficient);
                    mRenderer.setWorldOffset(mRenderer.getWorldOffset().add(delta));

                    // Scaling
                    float scaleFactor = (float) (calculatePointersDistance() / initialScaleDistance);
                    mCamera.setZoom(initialZoom * scaleFactor);

                    touchFocalPoint = newTouchFocalPoint;
                } else if (primaryPointer != null) {
                    final int pointerIndex = e.findPointerIndex(primaryPointer.pointerId);

                    final float x = e.getX(pointerIndex);
                    final float y = e.getY(pointerIndex);

                    final double dx = x - touchFocalPoint.getX();
                    final double dy = y - touchFocalPoint.getY();

                    final double cameraCoefficient = 0.0005d / mCamera.getZoom();
                    final Vector3 delta = new Vector3(dx, 0, dy).multiply(cameraCoefficient);
                    mRenderer.setWorldOffset(mRenderer.getWorldOffset().add(delta));

                    touchFocalPoint.setAll(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = e.getActionIndex();
                final int releasedPointerId = e.getPointerId(pointerIndex);

                if (secondaryPointer != null && releasedPointerId == secondaryPointer.pointerId) {
                    secondaryPointer = null;
                    touchFocalPoint = new Vector2(primaryPointer.lastTouchX, primaryPointer.lastTouchY);
                }

                if (primaryPointer != null && releasedPointerId == primaryPointer.pointerId) {
                    primaryPointer = null;

                    if (secondaryPointer != null) {
                        touchFocalPoint.setAll(secondaryPointer.lastTouchX, secondaryPointer.lastTouchY);
                        primaryPointer = secondaryPointer;
                        secondaryPointer = null;
                    }
                }

                initialScaleDistance = 0;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                primaryPointer = null;
                secondaryPointer = null;
                touchFocalPoint = null;
                break;
            }
        }
        return true;

    }

    private double calculatePointersDistance() {
        return calculateDistance(primaryPointer.lastTouchX, primaryPointer.lastTouchY, secondaryPointer.lastTouchX, secondaryPointer.lastTouchY);
    }

    private double calculateDistance(double A_x, double A_y, double B_x, double B_y) {
        double dx = Math.abs(A_x - B_x);
        double dy = Math.abs(A_y - B_y);
        return Math.sqrt(dx * dx + dy * dy);
    }
}
