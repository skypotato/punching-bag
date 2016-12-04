package org.skypotato.punchingbag;

import android.graphics.Paint;

/**
 * Created by hunso on 2016-11-20.
 * SharedPreference 부분
 */

public class TextObject {
    private String text;
    private boolean state;
    private Paint paint;
    private int x, y;
    private Thread thread;

    public TextObject(Paint paint, String text) {
        this.paint = paint;
        this.text = text;
        this.state = false;
        this.x = 0;
        this.y = 0;
        this.thread = null;
    }

    public void startThread() {
        thread = null;
        this.thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setState(false);
            }
        };
        thread.start();
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
