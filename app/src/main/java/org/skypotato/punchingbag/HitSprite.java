package org.skypotato.punchingbag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by hunso on 2016-10-17.
 * SharedPreference &#xbd80;&#xbd84;
 */
class HitSprite extends GraphicObject {
    private Rect mRect; // 사각영역
    private int miFrames; // 총프레임개수
    private int mCurrentFrame; // 최근프레임
    private int mSpriteWidth; // 프레임넓이
    private int mSpriteHeight; // 프레임높이
    private long mFrameTimer;

    private Bitmap mBitmap;
    private HitThread mThread;

    HitSprite(Bitmap bitmap) { // 초기화
        super(bitmap);
        mBitmap = bitmap;
        mThread = new HitThread(this);
        mRect = new Rect(0, 0, 0, 0);
        mFrameTimer = 0;
        mCurrentFrame = 0;
    }


    public int getmSpriteHeight() {
        return mSpriteHeight;
    }

    public int getmSpriteWidth() {
        return mSpriteWidth;
    }

    void InitSpriteDate(int fps, int iFrame) {
        mSpriteWidth = mBitmap.getWidth() / iFrame;
        mSpriteHeight = mBitmap.getHeight();
        mRect.top = 0;
        mRect.bottom = mSpriteHeight;
        mRect.left = 0;
        mRect.right = mSpriteWidth;
        miFrames = iFrame;
    }


    void Draw(Canvas canvas, int mx, int my) {
        Rect dest = new Rect(mx - (mSpriteWidth / 2), my - (mSpriteHeight / 2), mx + (mSpriteWidth / 2), my + (mSpriteHeight / 2));
        canvas.drawBitmap(mBitmap, mRect, dest, null);
    }

    private void UpDate(long GameTime) {
        if (GameTime > mFrameTimer) {
            mFrameTimer = GameTime;
            mCurrentFrame += 1;
            if (mCurrentFrame >= miFrames) {
                mCurrentFrame = 0;
            }
            mRect.left = mCurrentFrame * mSpriteWidth;
            mRect.right = mRect.left + mSpriteWidth;
        }
    }

    void startHitTread() {
        try {
            mThread.start();
        } catch (Exception e) {
            mThread = null;
            mThread = new HitThread(this);
            mThread.start();
        }
    }

    private class HitThread extends Thread {
        private HitSprite hitSprite;

        HitThread(HitSprite hitSprite) {
            this.hitSprite = hitSprite;
        }

        @Override
        public void run() {
            for (int i = 0; i < miFrames; i++) {
                try {
                    sleep(100);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                Update();
            }
            hitSprite.setState(false);
        }


        void Update() {
            long GameTime = System.currentTimeMillis();
            hitSprite.UpDate(GameTime);
        }
    }

}

