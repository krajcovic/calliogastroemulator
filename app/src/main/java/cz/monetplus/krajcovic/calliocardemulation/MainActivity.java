package cz.monetplus.krajcovic.calliocardemulation;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements IsoDepTransceiver.OnMessageReceived, NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private ListView listView;
    private IsoDepAdapter isoDepAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        isoDepAdapter = new IsoDepAdapter(getLayoutInflater());
        listView.setAdapter(isoDepAdapter);
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//
//        if(nfcAdapter == null || !nfcAdapter.isEnabled()) {
//            Toast.makeText(this.getApplicationContext(),"NFC is not available or disabled.", Toast.LENGTH_LONG).show();
//            finish();
//
//        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    @Override
    public void onResume() {
        super.onResume();
        if(nfcAdapter != null ) {
            nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(nfcAdapter != null ) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, this);
        Thread thread = new Thread(transceiver);
        thread.start();
    }

    @Override
    public void onMessage(final byte[] message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                isoDepAdapter.addMessage(new String(message));
            }
        });
    }

    @Override
    public void onError(Exception exception) {
        onMessage(exception.getMessage().getBytes());
    }
}
