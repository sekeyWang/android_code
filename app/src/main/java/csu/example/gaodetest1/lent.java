package csu.example.gaodetest1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class lent extends AppCompatActivity {
    Button revert;
    String id, namespace=resource.getNamespace();
    TextView tv;
    int ti = 0;
    String sti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lent);
        id = getIntent().getStringExtra("id");
        revert = (Button) findViewById(R.id.button2);
        revert.setOnClickListener(listener);
        tv = (TextView) findViewById(R.id.textView5);
        tv.setText("hello");
        timer.schedule(task, 0, 1000 );
    }
    Timer timer = new Timer(true);
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ti++;
                    sti = "已经租借了";
                    if (ti > 3600) sti += String.valueOf(ti / 3600) + "小时";
                    ti %= 3600;
                    if (ti > 60) sti += String.valueOf(ti / 60) + "分钟";
                    ti %= 60;
                    sti += String.valueOf(ti) + "秒";
                    tv.setText(sti);
                }
            });
        }
    };
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                StringBuilder buf = new StringBuilder();
                URL url = new URL("http://115.29.55.131:9191/device/" + id + "/operation?action=revert&namespace=" + namespace);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                if(conn.getResponseCode()==200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = in.readLine();
                    JSONObject jsonObj = new JSONObject(line);
                    if (jsonObj.optInt("code") == 200) {
                        Toast.makeText(getApplicationContext(),"归还成功", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(lent.this, MainActivity.class);
//                        myIntent.putExtra("token", jsonObj.optString("data"));
                        lent.this.startActivity(myIntent);
                    }
                    else Toast.makeText(getApplicationContext(), jsonObj.optString("data"), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "归还失败", Toast.LENGTH_LONG).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
