package cz.monetplus.krajcovic.calliocardemulation;

import android.app.Service;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class CallioHostApduService extends HostApduService {

    private int messageCounter = 0;


    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (selectAidApdu(apdu)) {
            Log.i("HCEDEMO", "Application selected");
            //return getWelcomeMessage();
            return getSkalakAnswer();
        } else {
            Log.i("HCEDEMO", "Received: " + new String(apdu));
//            return getNextMessage();
            return getOthers();
        }
    }

    private byte[] getSkalakAnswer() {
        //  6f 35 84 0b a0 00 00 00 28 31 01 02 00 02 01 a5 26 50 0c 43 41 4c 4c 49 4f 67 61 73 74 72 6f bf 0c 15 57 13 97 03 47 00 00 00 01 31 d1 51 20 00 00 00 00 00 00 00 0f
        byte[] answer = {0x6f, 0x35, (byte)0x84, 0x0b, (byte)0xa0, 0x00, 0x00, 0x00, 0x28, 0x31, 0x01, 0x02, 0x00, 0x02, 0x01, (byte)0xa5, 0x26, 0x50, 0x0c, 0x43, 0x41, 0x4c, 0x4c, 0x49, 0x4f, 0x67, 0x61, 0x73, 0x74, 0x72, 0x6f, (byte)0xbf, 0x0c, 0x15, 0x57, 0x13, (byte)0x97, 0x03, 0x47, 0x00, 0x00, 0x00, 0x01, 0x31, (byte)0xd1, 0x51, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0f, (byte)0x90, 0x00};
        return answer;
    }

    private byte[] getOthers() {
        byte[] answer = {0x6e, 0x00};
        return answer;

    }

    private byte[] getWelcomeMessage() {
        return "Hello Desktop!".getBytes();
    }

    private byte[] getNextMessage() {
        return ("Message from android: " + messageCounter++).getBytes();
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte) 0 && apdu[1] == (byte) 0xa4;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.i("HCEDEMO", "Deactivated: " + reason);
    }
}
