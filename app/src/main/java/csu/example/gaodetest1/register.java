package csu.example.gaodetest1;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class register extends AppCompatActivity {
    Button button1;
    EditText account, password;
    TextView register;
    static String namespace = resource.getNamespace();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//注意为“R.layout.activity_second”
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(listener);
        account = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        register = (TextView) findViewById(R.id.textView3);
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
    }
    public void handleOnClick(View v){
        Toast.makeText(getApplicationContext(), "注册", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(register.this, second_class.class);
        register.this.startActivity(myIntent);
    }
    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                StringBuilder buf = new StringBuilder();
                buf.append("account=" + account.getText().toString() + "&");
                buf.append("password=" + password.getText().toString() + "&");
                buf.append("namespace=" + namespace);
                byte[]data = buf.toString().getBytes("UTF-8");
                URL url = new URL("http://115.29.55.131:9191/user/register");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(data);
                if(conn.getResponseCode()==200){
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String result = in.readLine();
                    JSONObject jsonObj = new JSONObject(result);
                    if (jsonObj.optInt("code") == 200) {
                        Toast.makeText(getApplicationContext(),"注册成功", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(register.this, second_class.class);
                        register.this.startActivity(myIntent);
                    }
                    else Toast.makeText(getApplicationContext(), jsonObj.optString("data"), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
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
