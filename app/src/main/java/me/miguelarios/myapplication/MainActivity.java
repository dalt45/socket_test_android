package me.miguelarios.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            mSocket = IO.socket("https://ucp.uxmalstream.com:35810?__sails_io_sdk_version=1.1.13");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, mSocket.toString());
                if (mSocket != null) {
                    mSocket
                            .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Exception e = (Exception) args[0];
                                    Log.d(TAG, e.toString());
                                }
                            })
                            .on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(TAG, "connect");
                                    mSocket.emit("hello", "hello world");
                                    mSocket.disconnect();
                                }
                            })
                            .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Log.d(TAG, "disconnect");
                                }
                            })
                            .on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                                @Override
                                public void call(Object... args) {
                                    Transport transport = (Transport) args[0];
                                    // Adding headers when EVENT_REQUEST_HEADERS is called
                                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                                        @Override
                                        public void call(Object... args) {
                                            Log.v(TAG, "Caught EVENT_REQUEST_HEADERS after EVENT_TRANSPORT, adding headers");
                                            Map<String, List<String>> mHeaders = (Map<String, List<String>>) args[0];
                                            mHeaders.put("Authorization", Arrays.asList("Basic bXl1c2VyOm15cGFzczEyMw=="));
                                        }
                                    });
                                }
                            });
                    mSocket.connect();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
