package org.skypotato.punchingbag;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import static org.skypotato.punchingbag.R.raw.pain;

/**
 * Created by hunso on 2016-11-09.
 * SharedPreference 부분
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameThread mThread;

    private int punchNum = 8;
    private int manNum = 6;

    private Context mContext;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    /*화면크기*/
    private int width, height;

    /*비트맵이미지*/
    private Bitmap imgBack;
    private GraphicObject punchBag;
    private GraphicObject[] punch = new GraphicObject[punchNum];
    private HitSprite[] hitSprites = new HitSprite[punchNum];

    private ManSprite[] manSprites = new ManSprite[manNum];
    private ManThread[] manThreads = new ManThread[manNum];
    private HitSprite[] manDieSprites = new HitSprite[manNum];
    private Point spawn[] = new Point[manNum];
    /*효과음*/
    SoundPool sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    SoundPool soundPain = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int soundId;
    int soundId02;

    /*stage, combo,점수*/
    static int stage, combo, score;
    static float hp;
    float hpMAX;
    TextObject comboText; // comboText
    Paint paint = new Paint();

    int respawnNum; // 나오는 위치

    private long startTime;
    private long currentTime;
    private int lastTime;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);

        Display display = ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x; // 화면의 폭
        height = size.y; // 화면의 높이

        soundId = sound.load(mContext, R.raw.punch, 1); // 타격 효과음 load
        soundId02 = soundPain.load(mContext, pain, 1); // 피격 효과음 load
        mThread = new GameThread();

        score = 0;
        stage = 1;
        hpMAX = convertDpToPixel(250, mContext);
        hp = hpMAX;
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setTextSize(convertDpToPixel(20, mContext));
        paint.setTypeface(Typeface.create("", Typeface.BOLD));
        comboText = new TextObject(paint, "combo " + combo);

        startTime = System.currentTimeMillis();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mThread.getState() != Thread.State.RUNNABLE)
            mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    class GameThread extends Thread {

        private boolean mRun = true;// Thread 종료 flag
        private boolean wait = false;

        public GameThread() {

            Resources res = mContext.getResources(); // 리소스 읽기
            imgBack = BitmapFactory.decodeResource(res, R.drawable.background);
            imgBack = Bitmap.createScaledBitmap(imgBack, width, height, true);

            for (int i = 0; i < punch.length; i++) {
                if (i % 2 == 0) {
                    punch[i] = new GraphicObject(BitmapFactory.decodeResource(res, R.drawable.punch_left));
                } else {
                    punch[i] = new GraphicObject(BitmapFactory.decodeResource(res, R.drawable.punch_right));
                }
                punch[i].setState(true);
                punchPos(i);

                hitSprites[i] = new HitSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_boom));
                hitSprites[i].InitSpriteDate(1, 7);

            }

            punchBag = new GraphicObject(BitmapFactory.decodeResource(res, R.drawable.punchbag));

            manSprites[0] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man2_left));
            manSprites[0].InitSpriteDate(1, 6);
            manSprites[1] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man1_left));
            manSprites[1].InitSpriteDate(1, 8);
            manSprites[2] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man2_left));
            manSprites[2].InitSpriteDate(1, 6);
            manSprites[3] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man1_right));
            manSprites[3].InitSpriteDate(1, 8);
            manSprites[4] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man2_right));
            manSprites[4].InitSpriteDate(1, 6);
            manSprites[5] = new ManSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_man1_right));
            manSprites[5].InitSpriteDate(1, 8);

            for (int i = 0; i < manNum; i++) {
                if (i < 3) {
                    manDieSprites[i] = new HitSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_die_left));
                    manDieSprites[i].InitSpriteDate(1, 6);
                } else {
                    manDieSprites[i] = new HitSprite(BitmapFactory.decodeResource(res, R.drawable.sprite_die_right));
                    manDieSprites[i].InitSpriteDate(1, 6);
                }

            }
               /*man생성 포인트*/
            spawn[0] = new Point(0, 0);
            spawn[1] = new Point(0, height / 2);
            spawn[2] = new Point(0, height);
            spawn[3] = new Point(width, 0);
            spawn[4] = new Point(width, height / 2);
            spawn[5] = new Point(width, height);

        }

        public void punchPos(int position) {
            int x, y;

            x = (int) (Math.random() * (width - punch[position].getWidth()));
            y = (int) (Math.random() * (height - punch[position].getHeight()));

            punch[position].setX(x);
            punch[position].setY(y);
        }

        public void DrawScore(Canvas canvas) {
            float marginLeft, marginTop;
            marginLeft = convertDpToPixel(16, mContext);
            marginTop = convertDpToPixel(32, mContext);
            // HP
            paint.setColor(Color.WHITE);
            canvas.drawText("Stage " + stage, marginLeft, marginTop, paint);
            // Score
            paint.setColor(Color.WHITE);
            canvas.drawText("Score " + score, width / 2, marginTop, paint);

            float marginBottom, hpbarHeight;
            marginBottom = convertDpToPixel(50, mContext);
            hpbarHeight = convertDpToPixel(15, mContext);
            paint.setColor(Color.RED);
            canvas.drawText("HP", marginLeft, height - marginBottom, paint);
            if (hp > 0) {
                //hp--;
                paint.setColor(Color.RED);
                canvas.drawRect(marginBottom, height - marginBottom - hpbarHeight, marginBottom + hp, height - marginBottom, paint);
            }
        }

        public void DrawPunch(Canvas canvas) {
            for (int i = 0; i < punch.length; i++) {
                if (punch[i].isState())
                    punch[i].Draw(canvas);
                if (hitSprites[i].isState())
                    hitSprites[i].Draw(canvas, hitSprites[i].getX(), hitSprites[i].getY());
            }
        }

        public void DrawMan(Canvas canvas) {
            for (int i = 0; i < manSprites.length; i++) {
                if (manSprites[i].isState())
                    manSprites[i].Draw(canvas, manSprites[i].getX(), manSprites[i].getY());
                if (manDieSprites[i].isState())
                    manDieSprites[i].Draw(canvas, manDieSprites[i].getX(), manDieSprites[i].getY());
            }
        }

        public void DrawCombo(Canvas canvas) {
            if (comboText.isState()) {
                comboText.setText("combo " + combo);
                mCanvas.drawText(comboText.getText(), comboText.getX() + convertDpToPixel(8, mContext), comboText.getY(), comboText.getPaint());
                comboText.startThread();
            }
        }

        public void EventController() {
            int time;
            currentTime = System.currentTimeMillis();
            time = (int) ((currentTime - startTime) / 1000);
            GameOver();
            if(lastTime==0){
                lastTime=time;
            }
            if(time!=lastTime) {
                lastTime=time;
                hpDamge();
                if (time % 5 == 0) {
                    respawnMan();
                }
                if (time % 30 == 0) {
                    stage++;
                }
            }
        }

        public void hpDamge() {
            float hpDamge = 1 + (0.2f * stage);
            if (hpDamge <= 4)
                hpDamge = 4;
            hp -= convertDpToPixel(hpDamge, mContext);
        }

        public void respawnMan() {
            int temp = (int) (Math.random() * 6);
            while (temp == respawnNum) {
                temp = (int) (Math.random() * 6);
            }
            respawnNum = temp;
            startMan(respawnNum);
        }

        public void GameOver() {
            if (hp <= 0) {
                try {
                    stopThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void startMan(int i) {
            manSprites[i].setState(true);
            try {
                manThreads[i].start();
            } catch (Exception e) {
                manThreads[i] = null;
                manThreads[i] = new ManThread(manSprites[i], mContext, spawn[i], i, width, height);
                manThreads[i].start();
            }
        }

        @Override
        public void run() {
            while (mRun) {
                mCanvas = mHolder.lockCanvas();// canvas를 잠그고 버퍼 할당
                try {
                    synchronized (mHolder) { // 동기화 유지
                        mCanvas.drawBitmap(imgBack, 0, 0, null);
                        EventController();
                        DrawPunch(mCanvas);
                        DrawMan(mCanvas);
                        DrawScore(mCanvas);
                        DrawCombo(mCanvas);
                    }
                } finally {
                    if (mCanvas != null)
                        mHolder.unlockCanvasAndPost(mCanvas); // canvas의 내용을 View에 전송
                }
                synchronized (this) {
                    if (wait) {
                        try {
                            wait();
                        } catch (Exception e) {
                            // nothing
                        }
                    }
                }
            } // while
        }//run

        public void pauseThread() {
            wait = true;
            synchronized (this) {
                this.notify();
            }
        }

        public void resumeThread() {
            wait = false;
            synchronized (this) {
                this.notify();
            }
        }

        public void stopThread() throws InterruptedException {
            mRun = false;
            synchronized (this) {
                this.join();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect temp = new Rect();
            synchronized (mHolder) {
                int x = (int) event.getX();            // 클릭한 위치를 Rect()로 만듦
                int y = (int) event.getY();
                for (int i = manSprites.length - 1; i >= 0; i--) {
                    if (manSprites[i].getRect().contains(x, y) == true && manSprites[i].isState()) {
                        hitMan(i);
                        return true;
                    }
                }
                for (int i = punch.length - 1; i >= 0; i--) {
                    if (punch[i].getRect().contains(x, y) == true && punch[i].isState()) {
                        hitEvent(i, x, y);
                        return true;
                    }
                } // for
            } // synchronized
        } // if
        return true;
    }

    /*dip<==>pixel*/
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /*타격이벤트*/
    public void hitEvent(int position, int mx, int my) {
        float temp;
        sound.play(soundId, 1.0F, 1.0F, 1, 0, 1.0F);
        score += 10 + (combo / 10);
        combo += 1;
        temp = hp + convertDpToPixel(20, mContext);
        if (temp < hpMAX)
            hp = temp;
        else
            hp = hpMAX;
        mThread.punchPos(position);
        hitSprites[position].setState(true);
        hitSprites[position].setX(mx);
        hitSprites[position].setY(my);
        hitSprites[position].startHitTread();
        comboText.setState(true);
        comboText.setX(mx);
        comboText.setY(my);
    }

    /*사람이벤트*/
    public void hitMan(int position) {
        soundPain.play(soundId02, 1.0F, 1.0F, 1, 0, 1.0F);
        score -= 50;
        hp -= convertDpToPixel(10 + stage, mContext);
        combo = 0;
        manSprites[position].setState(false);
        try {
            manThreads[position].stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manDieSprites[position].setState(true);
        manDieSprites[position].setX(manSprites[position].getX());
        manDieSprites[position].setY(manSprites[position].getY());
        manDieSprites[position].startHitTread();

    }

 }
