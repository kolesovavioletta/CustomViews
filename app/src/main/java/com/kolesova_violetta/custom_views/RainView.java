package com.kolesova_violetta.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Цветной дождь, падающий в небо
 */
public class RainView extends View {
    private static final int[] sRainPalette = {
            0xffff0000, // red
            0xffFF4500, // orange
            0xffFFFF00, // yellow
            0xff00FF00, // green
            0xff00BFFF, // skyblue
            0xff0000FF, // blue
            0xff4B0082, // violet
            0xff000000, // black
    };
    private static final int SPEED = 2;
    private static final int DROP_LENGTH = 200;
    private static int BACKGROUND_COLOR;
    private final Paint mPaint = new Paint();
    private Random random = new Random();

    private int mRainWidth;
    private int mRainHeight;

    private List<Drop> mRain;

    public RainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        BACKGROUND_COLOR = getResources().getColor(android.R.color.white);
        mPaint.setColor(getResources().getColor(android.R.color.black));
        mPaint.setStrokeWidth(20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRainWidth = w;
        mRainHeight = h;

        int dropCount = random.nextInt(5) + 5; // 5 - 10
        mRain = new ArrayList<>(dropCount);
        for (int i = 0; i < dropCount; i++) {
            mRain.add(generateDrop());
        }
    }

    private Drop generateDrop() {
        int color = random.nextInt(sRainPalette.length);
        int x = random.nextInt(mRainWidth);
        return new Drop(10, color, x, mRainHeight-1);
    }

    /**
     * Создавать каплю или нет
     */
    private boolean needGenerateDrop() {
        int probability = random.nextInt(50);
        return (probability == 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(BACKGROUND_COLOR);

        spreadRain();
        drawRain(canvas);
        invalidate();
    }

    private void drawRain(Canvas canvas) {
        for (int i = 0; i < mRain.size(); i++) {
            Drop drop = mRain.get(i);
            drawDrop(canvas, drop);
        }
    }

    private void spreadRain() {
        ArrayList<Integer> removeIndexes = new ArrayList<>();
        for (int i = 0; i < mRain.size(); i++) {
            Drop drop = mRain.get(i);
            drop.updateCoordinates();
            if (drop.yHead <= -DROP_LENGTH) { // когда хвост уйдет за границу экрана
                if(needGenerateDrop()) {
                    mRain.set(i, generateDrop());
                } else {
                    removeIndexes.add(i);
                }
            }
        }

        for(int i = removeIndexes.size() - 1; i > 0; i--) {
            mRain.remove((int) removeIndexes.get(i));
        }

        if(needGenerateDrop()) {
            mRain.add(generateDrop());
        }
    }

    /**
     * Отрисовка капли, где голова насыщенного цвета, а к хвосту становится прозрачной.
     */
    private void drawDrop(Canvas canvas, Drop drop) {
        Shader shader = new LinearGradient(
                drop.xHead, drop.yHead, drop.getXEnd(), drop.getYEnd(),
                drop.getColorStart(), drop.getColorEnd(), Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
        canvas.drawLine(drop.xHead, drop.yHead, drop.getXEnd(), drop.getYEnd(), mPaint);
    }

    class Drop {
        int alpha;
        int colorIndex;
        int xHead;
        int yHead;

        Drop(int alpha, int colorIndex, int x, int y) {
            setAlpha(alpha);
            this.colorIndex = colorIndex;
            xHead = x;
            yHead = y;
        }

        void setAlpha(int alpha) {
            if (alpha < 1 || 10 < alpha) {
                alpha = 0;
            }
            this.alpha = alpha;
        }

        int getColorStart() {
            return sRainPalette[colorIndex];
        }

        int getColorEnd() {
            int baseColor = getColorStart();

            int a = 0; // 0%
            int r = 0xFF & (baseColor >> 16);
            int g = 0xFF & (baseColor >> 8);
            int b = 0xFF & (baseColor);
            return Color.argb(a, r, g, b);
        }

        int getXEnd() {
            return xHead;
        }

        int getYEnd() {
            return yHead + DROP_LENGTH;
        }

        void updateCoordinates() {
            yHead -= SPEED;
        }
    }
}
