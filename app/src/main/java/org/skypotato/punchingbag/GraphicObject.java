package org.skypotato.punchingbag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by hunso on 2016-10-17.
 */
class GraphicObject {

    private Bitmap bitmap;
    private Rect rect;

    private int width, height;
    private int x, y;

    private boolean state;


    GraphicObject(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        rect = new Rect(0, 0, 0, 0);
        x = 0;
        y = 0;
        state = false;
    }

    private void updateRectPosition() {
        rect = new Rect(x - (width / 2), y - (height / 2), x + (width / 2), y + (height / 2));
    }

    void Draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (width / 2), y - (height / 2), null);
    }

    boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Rect getRect() {
        return rect;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        updateRectPosition();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        updateRectPosition();
    }

}
