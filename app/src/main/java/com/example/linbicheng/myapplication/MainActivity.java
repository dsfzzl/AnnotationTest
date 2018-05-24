package com.example.linbicheng.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linbicheng.myapplication.annotation.TaHelper;
import com.face.jfshare.annotationlib.annotation.FindId;
import com.face.jfshare.annotationlib.annotation.OnClick;

@FindId(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @FindId(R.id.test)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        TaHelper.inject(this);

        textView.setText("woshoshsohdosh");
    }


    @OnClick({R.id.test,R.id.test1})
    public void oncliel(View view) {
        switch (view.getId()) {
            case R.id.test:
                Toast.makeText(this,"hhhhhhhh",Toast.LENGTH_SHORT).show();
                break;
            case R.id.test1:
                Toast.makeText(this,"dsfadsfasdf",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
