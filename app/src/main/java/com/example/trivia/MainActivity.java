package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private  Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private  ImageButton prevButton;
    private  int currentQuestionIndex=0;
    private List<Question> questionList;
    private int scoreCounter=0;
    private Score score;
    private TextView scoreText;
    private Prefs prefs;
    private TextView highestScore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score=new Score();
        prefs=new Prefs(MainActivity.this);
        nextButton=findViewById(R.id.next_Button);
        prevButton=findViewById(R.id.prev_Button);
        trueButton=findViewById(R.id.true_Button);
        falseButton=findViewById(R.id.false_Button);
        questionCounterTextView=findViewById(R.id.question_counter);
        questionTextView=findViewById(R.id.question_textview);
        scoreText=findViewById(R.id.scoreTextView);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        highestScore=findViewById(R.id.highest_score);
        //get previous state
        currentQuestionIndex=prefs.getState();



      highestScore.setText(MessageFormat.format("Highest Score:{0}", String.valueOf(prefs.getHighScore())));


        questionList= new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());

                Log.d("inside", "processFinished: "+ questionArrayList);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.prev_Button:
                if(currentQuestionIndex>0)
                {
                    currentQuestionIndex=(currentQuestionIndex-1)%questionList.size();
                    updateQuestion();

                }
                break;
            case R.id.next_Button:
               // prefs.saveHighestScore(scoreCounter);
                currentQuestionIndex=(currentQuestionIndex+1)%questionList.size();
                updateQuestion();
                break;
            case R.id.true_Button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_Button:
                checkAnswer(false);
                updateQuestion();
                break;

        }

    }

    private void checkAnswer(boolean b) {
        boolean answerIsTrue=questionList.get(currentQuestionIndex).isAnswerTrue();
        //int toastMessageId=0;
        if(b==answerIsTrue)
        {
            fadeView();
            addPoint();
            //toastMessageId=R.string.correct_answer;

        }
        else
        {
            shakeAnimation();
            substractPoint();
          //  toastMessageId=R.string.wrong_answer;
        }
       // Toast.makeText(MainActivity.this
        //,toastMessageId,Toast.LENGTH_LONG).show();

    }

    private void addPoint()
    {
         scoreCounter +=10;
         score.setScore(scoreCounter);
         scoreText.setText(String.valueOf(score.getScore()));

    }
    private  void substractPoint()
    {
        scoreCounter-=10;
        if(scoreCounter>0)
        {
            score.setScore(scoreCounter);
            scoreText.setText( String.valueOf(score.getScore()));

        }
        else {
            scoreCounter=0;
            score.setScore(scoreCounter);
            scoreText.setText( String.valueOf(score.getScore()));

        }


    }


    private void updateQuestion() {

        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex+1+" "+"out of"+" "+questionList.size());
    }

    private void fadeView()
    {
        final CardView  cardView=findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                cardView.setBackgroundColor(Color.WHITE);
              goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void shakeAnimation()
    {
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    public void goNext()
    {
        currentQuestionIndex=(currentQuestionIndex+1)%questionList.size();
        updateQuestion();


    }


    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}
