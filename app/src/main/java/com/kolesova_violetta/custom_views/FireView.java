package com.kolesova_violetta.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Random;

/**
 * Огонь.
 */
public class FireView extends View {

    private static final int[] sFirePalette = {
            0xff070707,
            0xff1F0707,
            0xff2F0F07,
            0xff470F07,
            0xff571707,
            0xff671F07,
            0xff771F07,
            0xff8F2707,
            0xff9F2F07,
            0xffAF3F07,
            0xffBF4707,
            0xffC74707,
            0xffDF4F07,
            0xffDF5707,
            0xffDF5707,
            0xffD75F07,
            0xffD75F07,
            0xffD7670F,
            0xffCF6F0F,
            0xffCF770F,
            0xffCF7F0F,
            0xffCF8717,
            0xffC78717,
            0xffC78F17,
            0xffC7971F,
            0xffBF9F1F,
            0xffBF9F1F,
            0xffBFA727,
            0xffBFA727,
            0xffBFAF2F,
            0xffB7AF2F,
            0xffB7B72F,
            0xffB7B737,
            0xffCFCF6F,
            0xffDFDF9F,
            0xffEFEFC7,
            0xffFFFFFF
    };
    private final Paint mPaint = new Paint();
    private final int TOUCH_RADIUS = 10;
    private final int STEP = 1;
    private Random random = new Random();
    private int[] mFirePixels;
    private int mFireWidth;
    private int mFireHeight;
    private float mScale;

    public FireView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mFireWidth = 300;
        mScale = (float) w / mFireWidth;
        mFireHeight = (int) (h / mScale);

        mFirePixels = new int[mFireWidth * mFireHeight];

        // paint the last line white
        int firstIndexOfDownLine = getIndex(0, mFireHeight - 1);
        int lastIndexOfDownLine = mFirePixels.length; // bounce not included
        int startColorIndex = sFirePalette.length - 1; // color white
        Arrays.fill(mFirePixels, firstIndexOfDownLine, lastIndexOfDownLine, startColorIndex);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = (int) (event.getX() / mScale);
                int y = (int) (event.getY() / mScale);
                showTouchOnFire(x, y);
                break;
        }

        invalidate();
        return true;
    }

    /**
     * Отображение косания
     *** _
     * /  \
     * \__/
     */
    private void showTouchOnFire(int x, int y) {
        int xStart = x - TOUCH_RADIUS, xEnd = x + TOUCH_RADIUS;

        int yStart = y;
        int yEnd = y;
        /*
          Расширение
                        _
                      /  \
         */
        for (int i = xStart; i < x; i++) {
            yStart -= STEP;
            yEnd += STEP;
            for (int j = yStart; j < yEnd; j++) {
                setFirePixel(i, j, 0);
            }
        }
        /*
          Сужение
                      \__/
         */
        for (int i = x; i < xEnd; i++) {
            yStart += STEP;
            yEnd -= STEP;
            for (int j = yStart; j < yEnd; j++) {
                setFirePixel(i, j, 0);
            }
        }
        // Плохочитаемая версия:
//                int yStart, yEnd;
//                int step = STEP;
//                boolean increment = true;
//                for (int i = xStart; i < xEnd; i++) {
//                    step = increment ? step + 1 : step - 1;
//                    yStart = y - step;
//                    yEnd = y + step;
//                    for (int j = yStart; j < yEnd; j++) {
//                        setFirePixel(i, j, 0);
//                    }
//                    if (step >= TOUCH_RADIUS) increment = false;
//                }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        spreadFire();
        drawFire(canvas);
        invalidate();
    }

    /**
     * Распространение огня. Случайно выбирается соседний пиксель -> меняется его цвет или сохраняется
     */
    private void spreadFire() {
        for (int y = 0; y < mFireHeight - 1; y++) {
            for (int x = 0; x < mFireWidth; x++) {
                int dx = random.nextInt(3); // 0 1 2
                int dy = random.nextInt(6); // 0 1 2 3 4 5
                int parent_x = Math.min(mFireWidth - 1, Math.max(x + dx - 1, 0));
                int parent_y = Math.min(mFireHeight - 1, y + dy);
                int dtemp = -(dx & 1); // 0 -1
                setFirePixel(x, y, getFirePixel(parent_x, parent_y) + dtemp);
            }
        }
    }

    /**
     * Отрисовка огня
     * Массив {@param mFirePixels} содержит индексы цветов.
     */
    private void drawFire(Canvas canvas) {
        int[] bitmapPixels = new int[mFirePixels.length];
        for (int y = 0; y < mFireHeight; y++) {
            for (int x = 0; x < mFireWidth; x++) {
                int temperature = getFirePixel(x, y);
                if (temperature < 0) {
                    temperature = 0;
                }
                if (temperature >= sFirePalette.length) {
                    temperature = sFirePalette.length - 1;
                }

                @ColorInt int color = sFirePalette[temperature];
                bitmapPixels[getIndex(x, y)] = color;
            }
        }

        canvas.scale(mScale, mScale);

        Bitmap bitmap = Bitmap.createBitmap(mFireWidth, mFireHeight, Bitmap.Config.RGB_565);
        bitmap.setPixels(bitmapPixels, 0, mFireWidth, 0, 0, mFireWidth, mFireHeight);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
    }

    private int getIndex(int x, int y) {
        return y * mFireWidth + x;
    }

    private int getFirePixel(int x, int y) {
        int index = getIndex(x, y);
        if (mFirePixels == null || index < 0 || mFirePixels.length < index) {
            return 0;
        }
        return mFirePixels[index];
    }

    private void setFirePixel(int x, int y, int val) {
        int index = getIndex(x, y);
        if (mFirePixels == null || index < 0 || mFirePixels.length < index) {
            return;
        }
        mFirePixels[index] = val;
    }
}
