package com.example.webserversocketexcersice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Socket socket = null;
    OutputStream out;
    DataOutputStream dout;
    ReceiveThread receiveThread;

    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.msg);
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
        receiveThread = new ReceiveThread("70.12.60.108",8888);

        if(receiveThread!=null){
            receiveThread.execute();
        }
    }

    @Override
    protected void onDestroy() {
        sendProgress("q");
        super.onDestroy();
    }
    void sendProgress(String text){
        final String msg = text;
        if(msg.equals("q")){
            receiveThread.cancel(true);
        }
        final Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dout = new DataOutputStream(out);
                if (dout != null) {
                    try {
                        dout.writeUTF(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendThread.start();
        editText.setText("");
    }
    public void SendClick(View v) throws IOException {
        sendProgress(editText.getText().toString());
    }
    class ReceiveThread extends AsyncTask<String,Object,Integer>{

        Socket socket1;
        InputStream in;
        DataInputStream din;
        String ip;
        int port;

        public ReceiveThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            String cmd = (String)values[0];
            Log.d("cmd",cmd);
            if(cmd.equals("socket")){
                socket = (Socket)values[1];
            }
            else {
                textView.setText(textView.getText().toString()  + (String) values[1] + "\n");

            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                socket1 = new Socket(ip,port);
                socket = socket1;
                in = socket1.getInputStream();
                Object[] container = {new String("socket"),socket1};
                publishProgress(container);
                din = new DataInputStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    final String str = din.readUTF();
                    Log.d("deceive", str);
                    System.out.println("receice thread is running");
                    publishProgress(new String("message"),str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
