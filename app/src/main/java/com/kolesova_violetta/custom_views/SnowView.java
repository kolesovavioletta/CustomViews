package com.kolesova_violetta.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 100 падающих снежинок
 */
public class SnowView extends View {
    public static final int SNOW_COUNT = 100;
    private final Paint mPaint = new Paint();
    private Random random = new Random();

    private List<Snowflake> mSnow;

    private int mSnowWidth;
    private int mSnowHeight;

    enum SnowflakeGenerateMode { // режимы генерации снежинок
        RANDOM_COORDINATE, // для отрисовки при создании view по всей области
        ZERO_COORDINATE // для отрисовки новой снежинки сверху области
    }

    public SnowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaint.setAlpha(90);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mSnowWidth = w;
        mSnowHeight = h;

        mSnow = new ArrayList<>(SNOW_COUNT);
        for (int i = 0; i < SNOW_COUNT; i++) {
            mSnow.add(generateSnowflake(SnowflakeGenerateMode.RANDOM_COORDINATE));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(android.R.color.transparent));

        drawSnow(canvas);
        invalidate();
    }

    private void drawSnow(Canvas canvas) {
        for (int i = 0; i < mSnow.size(); i++) {
            Snowflake curSnowflake = mSnow.get(i);
            drawSnowflake(canvas, curSnowflake);
            curSnowflake.updateCoordinates();
            if(mSnowWidth < curSnowflake.cx || mSnowHeight < curSnowflake.cy) {
                mSnow.set(i, generateSnowflake(SnowflakeGenerateMode.ZERO_COORDINATE));
            }
        }
    }

    private void drawSnowflake(Canvas canvas, Snowflake snowflake) {
        canvas.drawCircle(snowflake.cx, snowflake.cy, snowflake.r, mPaint);
    }

    private Snowflake generateSnowflake(SnowflakeGenerateMode mode) {
        Snowflake snowflake = new Snowflake();
        snowflake.cx = random.nextInt(mSnowWidth);
        switch (mode) {
            case RANDOM_COORDINATE:
                snowflake.cy = random.nextInt(mSnowHeight);
                break;
            case ZERO_COORDINATE:
                snowflake.cy = 0;
                break;
        }
        snowflake.dx = random.nextInt(7) - 3; //-3 to 3
        snowflake.dy = random.nextInt(5) + 1; // 1 to 5
        snowflake.r = random.nextInt(11) + 5; // 5 to 15
        return snowflake;
    }

    class Snowflake {
        int cx;
        int cy;
        int dx;
        int dy;
        int r;

        void updateCoordinates() {
            cx += dx;
            cy += dy;
        }
    }
}
