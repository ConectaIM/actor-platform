package im.actor.sdk;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by diego on 14/06/17.
 */

public class ClcIdentifier {

    private static String mac = null;

    public static String getMACAddress() {
        if (mac == null) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);

                byte[] macAddress = networkInterface.getHardwareAddress();

                StringBuilder sb = new StringBuilder();
                for (int byteIndex = 0; byteIndex < macAddress.length; byteIndex++) {
                    sb.append(String.format("%02X%s", macAddress[byteIndex], (byteIndex < macAddress.length - 1) ? "-" : ""));
                }
                mac = sb.toString();
                return mac;
            } catch (Exception e) {
                mac = null;
                return "unknow";
            }
        } else {
            return mac;
        }
    }
}
