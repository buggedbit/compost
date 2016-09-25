package friends.eevee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import friends.eevee.Calender.Date;
import friends.eevee.Calender.Time;


public class Scratch extends AppCompatActivity {

    EditText from_date;
    EditText to_date;
    EditText init_date;
    EditText add_date;
    EditText from_time;
    EditText to_time;
    EditText init_time;
    EditText add_time;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scratch);
        from_date = (EditText) findViewById(R.id.from_date);
        to_date = (EditText) findViewById(R.id.to_date);
        from_time = (EditText) findViewById(R.id.from_time);
        to_time = (EditText) findViewById(R.id.to_time);

        init_date = (EditText) findViewById(R.id.init_date);
        add_date = (EditText) findViewById(R.id.add_date);
        init_time = (EditText) findViewById(R.id.init_time);
        add_time = (EditText) findViewById(R.id.add_time);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve1(v);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve2(v);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve3(v);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solve4(v);
            }
        });
    }

    private void solve1(View v){
        String from_s = from_date.getText().toString();
        String to_s = to_date.getText().toString();
        Date from = new Date(from_s," ");
        Date to = new Date(to_s," ");
        textView1.setText(String.valueOf(to.dayDifferenceFrom(from)));
    }

    private void solve2(View v){
        String init_s = init_date.getText().toString();
        String add_s = add_date.getText().toString();
        Date init = new Date(init_s," ");
        init.addDays(Integer.valueOf(add_s));
        textView2.setText(String.valueOf(init.formalRepresentation()));
    }

    private void solve3(View v){
        String from_s = from_time.getText().toString();
        String to_s = to_time.getText().toString();
        Time from = new Time(from_s," ");
        Time to = new Time(to_s," ");
        textView3.setText(String.valueOf(to.timeDifferenceFrom(from)));
    }

    private void solve4(View v){
        String init_s = init_time.getText().toString();
        String add_s = add_time.getText().toString();
        Time init = new Time(init_s," ");
        init.addTime(Integer.valueOf(add_s));
        textView4.setText(String.valueOf(init.get12HrFormat()));
    }

}
