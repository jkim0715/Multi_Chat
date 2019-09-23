package com.example.webserversocketexcersice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    String msg;

    InputStream in;
    DataInputStream din;

    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.msg);

        Thread receiveThread = new Thread(new Runnable() {
            public void setStream() throws IOException {
                try {
                    socket = new Socket("70.12.60.108", 8888);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = socket.getInputStream();
                din = new DataInputStream(in);
            }
            @Override
            public void run() {
                try {
                    setStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        String str = din.readUTF();
                        textView.setText(textView.getText().toString()+"\n"+str);
                    }
                } catch (Exception e) {

                }


            }
        });
        receiveThread.start();
    }

    public void SendClick(View v) throws IOException {
        Thread sendThread = new Thread(new Runnable() {
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
                        dout.writeUTF(editText.getText().toString());
                        editText.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendThread.start();
    }
}
