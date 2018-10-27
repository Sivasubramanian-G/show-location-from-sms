package com.example.siva.latlonsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle=intent.getExtras();
        SmsMessage[] smsm=null;
        String sms_str="";
        String mess = "";

        if(bundle!=null){

            Object[] pdus=(Object[]) bundle.get("pdus");
            smsm=new SmsMessage[pdus.length];
            for(int i=0;i<smsm.length;i++){
                smsm[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                sms_str+="Sent From:"+smsm[i].getDisplayOriginatingAddress();
                sms_str+="\r\nMessage: ";
                sms_str+=smsm[i].getDisplayMessageBody().toString();
                mess+=smsm[i].getDisplayMessageBody().toString();
                sms_str+="\r\n";
            }

            Intent smsIntent=new Intent(context,MainActivity.class);
            smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            smsIntent.putExtra("sms_str",sms_str);
            smsIntent.putExtra("message",mess);
            String[] m = mess.split(" ");
            if(m[0].equals("Latitude:") && m[2].equals("Longitude:") && isNumeric(m[1]) && isNumeric(m[3])) {
                context.startActivity(smsIntent);
            }

        }

    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

}

