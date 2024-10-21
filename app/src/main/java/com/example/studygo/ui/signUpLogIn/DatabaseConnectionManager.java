package com.example.studygo.ui.signUpLogIn;

import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnectionManager {
    private static final String SSH_HOST = "92.112.187.2";
    private static final int SSH_PORT = 65002;
    private static final String SSH_USER = "u511097224";
    private static final String SSH_PRIVATE_KEY_PATH = "C:/Users/renpo/.ssh/id_rsa_priv"; // Add path to your private key

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    // The private key as a string
    private static final String PRIVATE_KEY =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIJKQIBAAKCAgEA8KZy8iFnHwXKGyHHhlwNTln/wAbpxR2iRFEXKbIV2rBzdoC8\n" +
                    "M2pipYUkwjqJXfchx2lwO+vl/W4kM1n0kiZrDAdkOv/UTlLqM1tuHBTKNiZcozYl\n" +
                    "571FCp8NtKdxaJo/sAYckhrVFyoX/JRwe0A1Bo8OSBOeANUmdreQ9ruNuVmdUsMP\n" +
                    "tysLOXXC1WhQuGI6fuHwzYclgBJ9oDvzkR9ki03ifr3ORcdM2QdzX8ohyxNPvT74\n" +
                    "iZz+0fZl8n2KXFnRb4iqew6AtcC27ddLTZdY+oY29GCpXWZh1o2gKa0bqYzZrKD4\n" +
                    "1+FPRiq1ZP7iHcdbcUxd1rLQL5SCSZmriwA35PPRn7sqGlxa5GIW7/+zRcWQ9Cna\n" +
                    "+LXLg3DsAqaMqr36eKdIh34Fx9KSNH2SqPzkE7B5kfOtCdvWPvOlddZfkRMxNzJ1\n" +
                    "bqebCjAVcNMMssJFMZdscJDsN4nOjsmQsjohkQ7ljVzTU+lqGJfBa1VI0mb6FgKB\n" +
                    "wix6DUJS3KahY32fQpOYUzp5c3qQj78eg01Zz/lg1ycfqHMWM2qYJMP9jrfHydM7\n" +
                    "hu3AaY8rJoFPNw9GOz63GeBCveRcTDgsV3/jauIwMLwH8OpYPj/kRlFID13MQ17u\n" +
                    "oPGwthGgLrtKufRSOi/0LH0anv9E3G+uW1gyXWBnvl6/6ZRyWKZxdOEw3A0CAwEA\n" +
                    "AQKCAgEAjbKkihfK/YROLc2lC2MWXqU1px8q9OrvCeTpi56VqbAuPH+u0WN59FZC\n" +
                    "f68yBUcn96AI+u96+8+ntyjnjkrayzlq9iZ/HyNJTbYvTo+bRWkvMNcIWLykc42V\n" +
                    "52KUr2/rwjzHA+ySvlIFz66j0J7M2jDPyvbqGTlorYyG6hEjcRjoPrwme8b3k/zY\n" +
                    "HQqQRRFpuh5VyN8j4JaCrB/A2hmu/St+Cvy+AW5JvJzbfXKqlUWotRjoBTeQ/5Zj\n" +
                    "DvaGyrwESvUg1NaESQDRdZhIecZ9GP6AS0LDVwCa6Vd5EDEi0aHi5fferla3c+xf\n" +
                    "5LkD8V7aZAsAUhP16545XRcD4BPf1GalvmN5D6pYIQWBqNyyXGPHoEZd3Uiyn8qi\n" +
                    "iDdzmCJBFSEpkEsQSYUkVoiEHlXpD3Z+mwwpLQfTeGGnHFdf3pWdFG243/VBRElP\n" +
                    "1wuZCg2DRXv2M2DHXwm80rwYkYqOk+BR4OEN+hHhnGVZxNaRh7t/if90PSqKb5Xc\n" +
                    "an7oDPrxtXC1p+J1DM/Yvymb7IeWDYmiUcFBu0S05QMmsqBzcwn4BiQdLqBP0K23\n" +
                    "YqQR9++P2is3/erLoZXqkV1u2TidP8m/lYjpnYhtwsiHq0igu3p6CPSdo+MKwKOZ\n" +
                    "HPhXg9LAd+BfESIApn0qo0qktDhPIzX7rmpsm/anQAUlFhGtugECggEBAP9dsCNA\n" +
                    "R/PUA+sYsGgX1eUUbx38KBP9G/6aeTYtrkSJMdLhRZjpA0p4PondrngI0hJAxCHa\n" +
                    "cHKFZBMwnMzLBl2o2ZDKsK/ioZn5A/48zU6wdrQocy+GBJHvJ1hZMQKvlqaAY29h\n" +
                    "2rfl6RC7k59heCnsIJPfQZxKNGS1UgmnIVQpRsw5E007CdWF22Mms/IsYM/XbWgH\n" +
                    "ax7fRB6clG2ujGBZturLrd0YLIZTZ3u4a0YbTK1XN2hC8VuTEagQpn61lqQwYYfE\n" +
                    "UW0CVGqFqXeX5etAr/xQjsJQxrpFG6KVxnOMFfCr916Si87LNwsNHH+458e+WLSJ\n" +
                    "tulMYfavHadXJI0CggEBAPE/aFS/Y8vzw941EBVrd0QpkVANyT6ijn/RCyrO2gt7\n" +
                    "dRSW6BD1mLsk+2SRLrOyVdJTMnc/OS2o0hX9WyCShMuFIMkfwvZTYdXzBsiypIHo\n" +
                    "hjgBArAUjjSD3xZV3vzQIcE7nsk/wCpBFKxWi03rc0geRm1JV/OFSSheEkvrZ9fb\n" +
                    "oJkefAlrPKdLLjJ8JPb5NCy3JThLf0LzqTwTg5sA23GKgasmVNvwvCic1XEYiZTa\n" +
                    "b8IZrr6ni0xlRa54e9LB+Hh2XS6ath4wSKviU165/9t/obJ15GEZXUjQzgAO6fmN\n" +
                    "CdhKEy+RsplsqQbTLj2WdbcGvC2qH/BhJCWXgOOfdYECggEAd8Fp7r4ggBo3VDMZ\n" +
                    "11cGOg3hJkCwUt9qF3Y9zciSZxWnxetbBiCQKIZHXcQWI7iPO3wf/nhAL5San902\n" +
                    "S1wSui2LCjnjrjgr7Nc4pivBZFug70/g5LKGxiFPZEE/mLDbh23CM+PB/GfXar9s\n" +
                    "tVuyTIatqGsUi8TI2UmnFVvJZ3yL9Q33HUrIi9fgUJk/3hwtsoqmhhikha/SdECd\n" +
                    "4unubujjvSyTdpH1n3DRoNWViSUYSloSY9Sw92764sQcMGPcJ1eBBXhtbx+eku3n\n" +
                    "0WvsqOyHG15z9HCJIaVzFkYC8QEK8PjUYwd6hRoL0CTv7n26RJPhaZkDrFjjwBnT\n" +
                    "YDyfqQKCAQBmDBZL0vGZ8jEmK3f0N5MpqBOps0svTX/NnVWF9B3TwZNS9EqR8fF7\n" +
                    "KDeW4LrKlGMtJrgwLPQ41jKXzqXfb1rw5A1sRh0oKTk3USfLeZWQxhUQV40Vgnxs\n" +
                    "xmN1hTlf44KDxH50Tp0wwnrGM42VlkUbeoL784xanETH6JMAvkEc0/BpFLYPHed4\n" +
                    "NJsEC/Nu7Rce2xHJcFYSshMiMHb4+DHbI2WaSgqXRouw+jZnMqPv7T5c8pQyXZjU\n" +
                    "5fnFxX1LN3WtLxRXhGYI+w+idYgih7cs2B5D6Q6D7EDDbl/VMnnSb9B8R0k6U/0i\n" +
                    "XQ5arrXeo2iD1TuAbXg5LD4oEKAaqPcBAoIBAQDm9AM/RL7DOyfl6gTD4se22cEi\n" +
                    "QWjZZ45WeWUga0YzTT1KgeuB8ERhqZDmUCwieWBTbADek3uU+Zc/h0gscou27ZS8\n" +
                    "F5nolPPqRwydkq4OuYRlRZHww9EpOy9jOZ9e9N9rqy7xFalpp5n+VgtQ/YkswBa4\n" +
                    "s3wbswoHgsRpOovWxF2GTLavxvYJz/aBpxLiYDAzOKxrssyMV3/k8Ipfp3XAMyhP\n" +
                    "+JinWWdhLJGW4HcN3cggjsTQIHIVMzZGsfruOrO1IVb9u4xlvV/Rxl8eC0wVfOji\n" +
                    "ZemN9sZgTmEHrTItYFhejBIqKH9QhEEqrOEe3VuDqkdOEXuSI3YsaBy8A2YD\n" +
                    "-----END RSA PRIVATE KEY-----\n";
    // Method to perform SSH connection with callback
    public void connectToDatabase(ConnectionCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Session session = null;
                try {
                    // Set up JSch session to the SSH server
                    JSch jsch = new JSch();
                    // Load the private key for SSH authentication

                    // Load the private key directly from the string
                    byte[] privateKeyBytes = PRIVATE_KEY.getBytes(); // Convert string to byte array
                    ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(privateKeyBytes);
                    jsch.addIdentity("id_rsa", privateKeyBytes, null, null);


                    session = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);

                    // Disable strict host key checking for simplicity
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.connect();
                    System.out.println(session.isConnected());
                    // Here you would typically open a tunnel or handle database connection
                    // For now, let's simulate successful connection
                    callback.onSuccess();
                } catch (JSchException e) {
                    callback.onFailure("SSH Error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                    }
                }
            }
        });
    }

    // Method to shut down the executor service
    public void shutDown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
