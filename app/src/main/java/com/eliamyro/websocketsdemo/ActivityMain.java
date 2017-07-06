package com.eliamyro.websocketsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ActivityMain extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button btnStart;

    @BindView(R.id.tv_msg)
    TextView tvMsg;

    private OkHttpClient client;

    private class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("Hello my name is Elias");
            webSocket.send("I live in Thessaliniki");
            webSocket.send(ByteString.decodeHex("deadbeef"));
            webSocket.close(NORMAL_CLOSE_STATUS, "Message sent!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            displayMessage("Receiving: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            displayMessage("Receiving bytes: " + bytes.hex());
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSE_STATUS, null);
            displayMessage("Closing: " + code + ", " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            displayMessage("Failure: " + t.getMessage() + ", " + response);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        client = new OkHttpClient();

    }

    @OnClick(R.id.btn_start)
    public void startButtonClicked(){
        makeRequest();
    }

    private void makeRequest(){
        Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket webSeocket = client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
    }

    private void displayMessage(final String message){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMsg.setText(tvMsg.getText().toString() + "\n\n" + message);
            }
        });
    }
}
