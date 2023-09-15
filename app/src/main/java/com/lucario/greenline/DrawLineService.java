package com.lucario.greenline;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class DrawLineService extends Service {
    private WindowManager windowManager;
    private final int lineWidth = MainActivity.lineWidth;
    private final int color = MainActivity.color;
    private final boolean flicker = MainActivity.flickerB;
    private final boolean rgb = MainActivity.isRGB;
    private View overlayView;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationChannel chan = new NotificationChannel(
                "MyChannelId",
                "Overlay",
                NotificationManager.IMPORTANCE_HIGH);
        chan.setDescription("For Foreground Service");
        // Start foreground service
        Notification notification = new NotificationCompat.Builder(this,"MyChannelId" )
                .setContentTitle("Overlay Service")
                .setContentText("Drawing overlay line")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .build();

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(chan);

        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    public int getNavigationBarHeight() {
        int result = 0;
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getStatusBarHeight() {
        int result = 0;
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate() {
        super.onCreate();

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.line_layout, null);

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        ImageView lineView = overlayView.findViewById(R.id.line_view);
        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels+getNavigationBarHeight()+getStatusBarHeight()+200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);

        canvas.drawLine((int)metrics.widthPixels/2f, 0, (int)metrics.widthPixels/2f, metrics.heightPixels+250, paint);


        lineView.setImageBitmap(bitmap);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                metrics.widthPixels + 50,
                metrics.heightPixels + 250,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        params.format = 1;
        params.flags=1848;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            params.alpha = ((InputManager)getSystemService(INPUT_SERVICE)).getMaximumObscuringOpacityForTouch();
        }
        // Add view to window manager
        windowManager.addView(overlayView, params);

        if(flicker){
            int delay = 0;
            int period = 50;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {
                    n++;
                    Bitmap bitmap = getRandomBitmap(metrics.heightPixels+getNavigationBarHeight()+getStatusBarHeight()+200, metrics.widthPixels, n);
                   new Handler(Looper.getMainLooper()).post(()-> lineView.setImageBitmap(bitmap));
                }
            }, delay, period);
        }
    }

    private int n = 0;

    private Bitmap getRandomBitmap(int height, int width, int n){
        Random random = new Random();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);

        canvas.drawLine((int)width/2f, 0, (int)width/2f, height, paint);

        if(n%10==0){
            for(int i = 0; i < ThreadLocalRandom.current().nextInt(0,10); i++){
                int startX2 = ThreadLocalRandom.current().nextInt(0, width);
                int startY2 = 0;
                int gapHeight2 = 10;
                paint.setStrokeWidth(ThreadLocalRandom.current().nextInt(5,20));
                while (startY2 < height) {
                    if(rgb){
                        paint.setARGB(255, ThreadLocalRandom.current().nextInt(0,256), ThreadLocalRandom.current().nextInt(0,256), ThreadLocalRandom.current().nextInt(0,256));
                    }
                    int startYPos = startY2;
                    int endYPos = startY2 + random.nextInt(gapHeight2) + 1; // Randomly calculate gap position

                    canvas.drawLine(startX2, startYPos, startX2, endYPos, paint);

                    startY2 = endYPos + random.nextInt(gapHeight2) + 1; // Move the start position to the end of the gap
                }
            }
        }

        return bitmap;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(overlayView);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}