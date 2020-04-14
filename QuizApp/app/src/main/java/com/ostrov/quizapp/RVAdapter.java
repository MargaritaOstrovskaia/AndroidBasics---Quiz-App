package com.ostrov.quizapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.QuestionViewHolder>{
    private ArrayList<Question> questions;
    private Context context;
    private boolean isChecked;

    RVAdapter(ArrayList<Question> questions, Context context){
        this.context = context;
        this.questions = questions;
        this.isChecked = false;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_rv, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionViewHolder holder, int position) {
        class AnswerCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
            private Question question;
            private int id;

            private AnswerCheckedChangeListener(Question question, int id) {
                this.question = question;
                this.id = id;
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                question.setSelectedAnswer((id));
            }
        }
        // clear view attributes
        holder.rg.clearCheck();
        holder.answerA.setBackgroundColor(0);
        holder.answerB.setBackgroundColor(0);
        holder.answerC.setBackgroundColor(0);
        holder.img.setVisibility(View.GONE);

        // set new data
        if (position % 2 == 0)
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.cardBackgroundColor1));
        else
            holder.cv.setCardBackgroundColor(context.getResources().getColor(R.color.cardBackgroundColor2));

        Question question = questions.get(position);
        String q = context.getString(R.string.q, position + 1, question.getQuestion());
        holder.question.setText(q);

        String a = context.getString(R.string.a, question.getAnswers().get(0));
        holder.answerA.setText(a);
        holder.answerA.setOnCheckedChangeListener(new AnswerCheckedChangeListener(question, 1));

        String b = context.getString(R.string.b, question.getAnswers().get(1));
        holder.answerB.setText(b);
        holder.answerB.setOnCheckedChangeListener(new AnswerCheckedChangeListener(question, 2));

        String c = context.getString(R.string.c, question.getAnswers().get(2));
        holder.answerC.setText(c);
        holder.answerC.setOnCheckedChangeListener(new AnswerCheckedChangeListener(question, 3));

        switch (question.getSelectedAnswer()) {
            case 1:
                holder.answerA.setChecked(true);
                break;
            case 2:
                holder.answerB.setChecked(true);
                break;
            case 3:
                holder.answerC.setChecked(true);
                break;
        }

        if (isChecked) {
            if (question.getCorrectAnswerNumber() != question.getSelectedAnswer())
                setColor(holder, question.getSelectedAnswer(), context.getResources().getColor(R.color.wrongColor));
            setColor(holder, question.getCorrectAnswerNumber(), context.getResources().getColor(R.color.rightColor));
            holder.rg.setEnabled(false);
        }

        String img = question.getImageName();
        if (!img.isEmpty()) {
            try {
                InputStream stream = context.getAssets().open(img);
                TypedValue typedValue = new TypedValue();
                typedValue.density = TypedValue.DENSITY_DEFAULT;
                Drawable d = Drawable.createFromResourceStream(null, typedValue, stream, img);

                holder.img.setImageDrawable(d);
                holder.img.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setColor(QuestionViewHolder holder, int num, int color) {
        switch (num) {
            case 1:
                holder.answerA.setBackgroundColor(color);
                break;
            case 2:
                holder.answerB.setBackgroundColor(color);
                break;
            case 3:
                holder.answerC.setBackgroundColor(color);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    void setIsChecked() {
        this.isChecked = true;
        this.notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        RadioGroup rg;

        TextView question;
        RadioButton answerA;
        RadioButton answerB;
        RadioButton answerC;
        ImageView img;

        QuestionViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            rg = itemView.findViewById(R.id.rg);
            question = itemView.findViewById(R.id.question_text_view);

            answerA = itemView.findViewById(R.id.answer_a_button);
            answerB = itemView.findViewById(R.id.answer_b_button);
            answerC = itemView.findViewById(R.id.answer_c_button);
            img = itemView.findViewById(R.id.image_view);
        }
    }

}