package com.kolesova_violetta.custom_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Button dotsButton = findViewById(R.id.dots_b);
        Button fireButton = findViewById(R.id.fire_b);
        Button snowButton = findViewById(R.id.snow_b);
        Button rainButton = findViewById(R.id.rain_b);

        dotsButton.setOnClickListener(v -> replaceFragment(new DotsFragment()));
        fireButton.setOnClickListener(v -> replaceFragment(new Fragment(R.layout.view_fire)));
        snowButton.setOnClickListener(v -> replaceFragment(new Fragment(R.layout.view_snow)));
        rainButton.setOnClickListener(v -> replaceFragment(new Fragment(R.layout.view_rain)));
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public static class DotsFragment extends Fragment {

        private ImageView left;
        private ImageView right;
        private ImageView center;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_balls, container, false);
            left = root.findViewById(R.id.imageViewLeft);
            right = root.findViewById(R.id.imageViewRight);
            center = root.findViewById(R.id.imageViewCenter);
            return root;
        }

        @Override
        public void onStart() {
            super.onStart();

            int durationMillis = 3000;

            RotateAnimation r = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, -0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            r.setDuration(durationMillis);
            r.setInterpolator(new LinearInterpolator());
            r.setRepeatMode(Animation.REVERSE);
            r.setRepeatCount(Animation.INFINITE);
            left.startAnimation(r);

            RotateAnimation rC = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rC.setDuration(durationMillis);
            rC.setInterpolator(new LinearInterpolator());
            rC.setRepeatMode(Animation.REVERSE);
            rC.setRepeatCount(Animation.INFINITE);
            right.startAnimation(rC);

            float startSize = 0.5f;
            float endSize = 1;
            float centerSelf = 0.5f;
            int durationMillisForCenter = durationMillis / 2;
            Animation s = new ScaleAnimation(startSize, endSize, startSize, endSize,
                    Animation.RELATIVE_TO_SELF, centerSelf,
                    Animation.RELATIVE_TO_SELF, centerSelf);
            s.setDuration(durationMillisForCenter);
            s.setInterpolator(new DecelerateInterpolator());
            s.setRepeatMode(Animation.REVERSE);
            s.setRepeatCount(Animation.INFINITE);
            center.startAnimation(s);
        }

        @Override
        public void onStop() {
            super.onStop();
            left.clearAnimation();
            right.clearAnimation();
            center.clearAnimation();
        }
    }
}