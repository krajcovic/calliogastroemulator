package cz.monetplus.krajcovic.calliocardemulation;

import android.nfc.tech.IsoDep;

import java.io.IOException;

/**
 * Created by krajcovic on 11/10/15.
 */
public class IsoDepTransceiver implements Runnable {


    public interface OnMessageReceived {

        void onMessage(byte[] message);

        void onError(Exception exception);
    }

    private IsoDep isoDep;
    private OnMessageReceived onMessageReceived;

    public IsoDepTransceiver(IsoDep isoDep, OnMessageReceived onMessageReceived) {
        this.isoDep = isoDep;
        this.onMessageReceived = onMessageReceived;
    }

    private static final byte[] CLA_INS_P1_P2 = { 0x00, (byte)0xA4, 0x04, 0x00 };

//    a0 00 00 00 28 31 01 02 00 02 01
    private static final byte[] AID_ANDROID = { (byte)0xA0, 0x00, 0x00, 0x00, 0x28, 0x31, 0x01 , 0x02, 0x00 , 0x02, 0x01};

    private byte[] createSelectAidApdu(byte[] aid) {
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte)aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;
    }

    @Override
    public void run() {
        int messageCounter = 0;
        try {
            isoDep.connect();
            byte[] response = isoDep.transceive(createSelectAidApdu(AID_ANDROID));
            while (isoDep.isConnected() && !Thread.interrupted()) {
                String message = "Message from IsoDep " + messageCounter++;
                response = isoDep.transceive(message.getBytes());
                onMessageReceived.onMessage(response);
            }
            isoDep.close();
        }
        catch (IOException e) {
            onMessageReceived.onError(e);
        }
    }
}