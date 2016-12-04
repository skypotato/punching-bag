package org.skypotato.punchingbag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by hunso on 2016-10-17.
 */
public class ManSprite extends GraphicObject {
    private Rect mRect; // 사각영역
    private int miFrames; // 총프레임개수
    private int mCurrentFrame; // 최근프레임
    private int mSpriteWidth; // 프레임넓이
    private int mSpriteHeight; // 프레임높이
    private long mFrameTimer;

    private Bitmap mBitmap;

    public ManSprite(Bitmap bitmap) { // 초기화
        super(bitmap);
        mBitmap = bitmap;
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

    public void InitSpriteDate(int fps, int iFrame) {
        mSpriteWidth = mBitmap.getWidth() / iFrame;
        mSpriteHeight = mBitmap.getHeight();
        mRect.top = 0;
        mRect.bottom = mSpriteHeight;
        mRect.left = 0;
        mRect.right = mSpriteWidth;
        miFrames = iFrame;
    }


    public void Draw(Canvas canvas, int mx, int my) {
        Rect dest = new Rect(mx - (mSpriteWidth / 3), my - (mSpriteHeight / 3), mx + (mSpriteWidth / 3), my + (mSpriteHeight / 3));
        Rect rect = new Rect(mx - (mSpriteWidth / 4), my - (mSpriteHeight / 4), mx + (mSpriteWidth / 4), my + (mSpriteHeight / 4));
        setRect(rect);
        canvas.drawBitmap(mBitmap, mRect, dest, null);
    }

    public void UpDate(long GameTime) {
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
}

