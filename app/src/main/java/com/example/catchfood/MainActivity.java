package com.example.catchfood;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;
    private ImageView character, spike, food, poison;
    private Drawable imageChar;

    private int charSize;

    private float charX, charY;
    private float spikeX, spikeY;
    private float foodX, foodY;
    private float poisonX, poisonY;

    private TextView scoreLabel, highScoreLabel;
    private int score, hScore, timeCount;

    private Timer timer;
    private Handler handler = new Handler();

    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean poison_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frame = findViewById(R.id.frame);
        startLayout = findViewById(R.id.startLayout);
        character  = findViewById(R.id.character);
        food = findViewById(R.id.food);
        poison = findViewById(R.id.poison);
        spike = findViewById(R.id.spike);
        scoreLabel = findViewById(R.id.ScoreLabel);
        highScoreLabel = findViewById(R.id.HighScore);
        imageChar = getResources().getDrawable(R.drawable.character);

    }
    int foodSpeed =12;
    int poisonSpeed = 20;
    public void changePos(){
        timeCount +=40;
        foodY +=foodSpeed;
        float foodCentreX = foodX + food.getWidth()/2;
        float foodCentreY = foodY + food.getHeight()/2;
        if(hitCheck(foodCentreX, foodCentreY))
        {
            foodY = frameHeight +100;
            score +=10;
        }
        if (foodY > frameHeight){
            foodY = -100;
            foodX = (float) Math.floor(Math.random() * (frameWidth-food.getWidth()));
        }
        food.setX(foodX);
        food.setY(foodY);


        spikeY +=18;
        float spikeCentreX = spikeX + spike.getWidth()/2;
        float spikeCentreY = spikeY + spike.getHeight()/2;

        if(hitCheck(spikeCentreX, spikeCentreY)){
            spikeY = frameHeight+ 100; //disappears to the bottom
            frameWidth = frameWidth * 80/100;
            changeFrameWidth(frameWidth);
            if(frameWidth < charSize){
               //quitGame();
                gameOver();
            }

        }
        if(spikeY > frameHeight){
            spikeY = -100;
            spikeX = (float) Math.floor(Math.random() * (frameWidth - spike.getWidth()));
        }
        spike.setX(spikeX);
        spike.setY(spikeY);



        if(!poison_flag && timeCount%10000 ==0){
            poison_flag = true;
            poisonY = -20;
            poisonX= (float) Math.floor(Math.random() * (frameWidth - poison.getWidth()));
        }

        if(poison_flag){
            poisonY +=poisonSpeed;
            float poisonCentreX = poisonX + poison.getWidth()/2;
            float poisonCentreY = poisonY + poison.getHeight()/2;

            if(hitCheck(poisonCentreX, poisonCentreY)){
                poisonY = frameHeight +30;
                score -=10;
                foodSpeed +=5;

            }
            if(poisonY >frameHeight) poison_flag = false;
            poison.setX(poisonX);
            poison.setY(poisonY);
        }






        if(action_flg){
            charX +=14;
        }
        else{
            charX -= 14;
        }
        //checking position of char
        if(charX <0 ){
            charX =0;
        }
        if(frameWidth - charSize < charX){
            charX = frameWidth - charSize;
        }
        character.setX(charX);
        scoreLabel.setText("Score: " + score);
    }

    public void gameOver(){
        timer.cancel();
        timer = null;
        start_flg=false;
        try{
            TimeUnit.SECONDS.sleep(1);

        }catch(InterruptedException e){

        }
        changeFrameWidth(initialFrameWidth);
        startLayout.setVisibility(View.VISIBLE);
        character.setVisibility(View.INVISIBLE);
        spike.setVisibility(View.INVISIBLE);
        poison.setVisibility(View.INVISIBLE);
        food.setVisibility(View.INVISIBLE);
        highScoreLabel.setText("High Score :" + score);
    }

    public boolean hitCheck(float x, float y){
        if(charX <= x && x <= charX + charSize && charY <=y && y <= frameHeight){
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int width){
        ViewGroup.LayoutParams params = frame.getLayoutParams();
        params.width = width;
        frame.setLayoutParams((params));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flg){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                action_flg = false;
            }
        }
        return true;
    }

    public void startGame(View view){
        start_flg = true;
        startLayout.setVisibility(view.INVISIBLE);

        if(frameHeight ==0){
            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();
            initialFrameWidth = frameWidth;

            charSize = character.getHeight();
            charX = character.getX();
            charY = character.getY();
        }
        character.setX(0.0f);
        spike.setY(3000.0f);
        poison.setY(3000.0f);
        food.setY(3000.0f);
        spikeY = spike.getY();
        poisonY = poison.getY();
        foodY = food.getY();

        character.setVisibility((view.VISIBLE));
        poison.setVisibility((view.VISIBLE));
        spike.setVisibility((view.VISIBLE));
        food.setVisibility((view.VISIBLE));

        timeCount = 0;
        score =0;
        scoreLabel.setText("Score: 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);

    }
    public void quitGame(View view){
        gameOver();
    }
}
