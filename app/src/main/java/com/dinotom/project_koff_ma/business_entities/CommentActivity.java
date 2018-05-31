package com.dinotom.project_koff_ma.business_entities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dinotom.project_koff_ma.R;

public class CommentActivity extends AppCompatActivity
{
    private static final String TAG = CommentActivity.class.getSimpleName();

    private EditText commentEditText;
    private Button commentApplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentEditText = findViewById(R.id.commentInput);
        commentApplyButton = findViewById(R.id.commentApplyButton);

        commentApplyButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String comment = commentEditText.getText().toString();
                Log.d(TAG, String.format("Comment: %s", comment));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("comment", comment);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Intent intent = getIntent();
        String comment = intent.getStringExtra("Comment");
        commentEditText.setText(comment);

        Toolbar commentActivityToolbar = (Toolbar) findViewById(R.id.comment_activity_appbar);
        commentActivityToolbar.setTitle("Komentiraj");
        setSupportActionBar(commentActivityToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
