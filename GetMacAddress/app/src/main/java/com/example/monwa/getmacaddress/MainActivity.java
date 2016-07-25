package com.example.monwa.getmacaddress;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onClick1(View v) {
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
        //startActivity(intent);
        TextView textView1 = (TextView) findViewById(R.id.txtResult) ;
        //String macAddress=getCurrentMacAddress();
        String macAddress=getMACAddress("wlan0");
        //String macAddress=getMACAddress("eth0");
        textView1.setText(macAddress) ;
    }

    public void onClick2(View v) {
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
        //startActivity(intent);
        TextView textView1 = (TextView) findViewById(R.id.txtResult) ;
        //String ipAddress=getLocalIpAddress();
        String ipAddress=getIPAddress(true); // IPv4
        //String ipAddress=getIPAddress(false); //// IPv6
        textView1.setText(ipAddress);
    }
/*
    public String getCurrentMacAddress(){
        String macAddress="";
        boolean bIsWifiOff=false;

        WifiManager wfManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if(!wfManager.isWifiEnabled()){
            wfManager.setWifiEnabled(true);
            bIsWifiOff = true;
        }

        WifiInfo wfInfo = wfManager.getConnectionInfo();
        macAddress = wfInfo.getMacAddress();

        if(bIsWifiOff){
            wfManager.setWifiEnabled(false);
            bIsWifiOff = false;
        }

        return macAddress;
    }
*/

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }
/*
    public String getLocalIpAddress()
    {
        final String IP_NONE = "N/A";
        final String WIFI_DEVICE_PREFIX = "eth";

        String LocalIP = IP_NONE;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if( LocalIP.equals(IP_NONE) )
                            LocalIP = inetAddress.getHostAddress().toString();
                        else if( intf.getName().startsWith(WIFI_DEVICE_PREFIX) )
                            LocalIP = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            //Log.e(DEBUG_TAG, "getLocalIpAddress Exception:"+e.toString());
        }
        return LocalIP;
    }
*/


    /**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
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
