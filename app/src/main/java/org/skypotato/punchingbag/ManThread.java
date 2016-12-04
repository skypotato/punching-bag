package org.skypotato.punchingbag;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by hunso on 2016-11-12.
 * SharedPreference 부분
 */

public class ManThread extends Thread {
    private Context mContext;
    private ManSprite manSprite;
    private int width, height;
    private int type;
    private int move;
    private boolean mRun = true;

    public ManThread(ManSprite manSprite, Context context, Point point, int type, int width, int height) {
        this.mContext = context;
        this.manSprite = manSprite;
        this.type = type;
        this.width = width;
        this.height = height;
        manSprite.setX(point.x);
        manSprite.setY(point.y);
        move = (int) GameView.convertDpToPixel(5, mContext);
    }

    @Override
    public void run() {
        while (mRun) {
            if (manSprite.getX() < 0 || manSprite.getX() > width || manSprite.getY() < 0 || manSprite.getY() > height)
                mRun = false;
            try {
                sleep(100);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            switch (type) {
                case 0: //왼쪽 위 출현
                    manSprite.setX(manSprite.getX() + move);
                    manSprite.setY(manSprite.getY() + move);
                    break;
                case 1: //왼쪽 중간 출현
                    manSprite.setX(manSprite.getX() + move);
                    break;
                case 2: //왼쪽 아래 출현
                    manSprite.setX(manSprite.getX() + move);
                    manSprite.setY(manSprite.getY() - move);
                    break;
                case 3: //오른쪽 위 출현
                    manSprite.setX(manSprite.getX() - move);
                    manSprite.setY(manSprite.getY() + move);
                    break;
                case 4: //오른쪽 중간 출현
                    manSprite.setX(manSprite.getX() - move);
                    break;
                case 5: //오른쪽 아래 출현
                    manSprite.setX(manSprite.getX() - move);
                    manSprite.setY(manSprite.getY() - move);
                    break;
            }
            long GameTime = System.currentTimeMillis();
            manSprite.UpDate(GameTime);
        }
        manSprite.setState(false);
    }
    public void stopThread() throws InterruptedException {
        mRun = false;
        synchronized (this) {
            this.join();
        }
    }

}
