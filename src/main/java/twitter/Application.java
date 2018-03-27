package twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
            try {
                String url = "www.google.com/skdhbfks";
                long time = System.currentTimeMillis();
                String temp = time + url;
                // Sets algorithm to SHA256
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                // Applies SHA256 to our input
                byte[] nonce = digest.digest(temp.getBytes("UTF-8"));
                // Makes the hexString mutable
                StringBuffer hexString = new StringBuffer();
                // Converts hash to hexString
                for (int i = 0; i < nonce.length; i++) {
                    String hex = Integer.toHexString(0xff & nonce[i]);
                    if (hex.length() == 1) {hexString.append(0);}
                    hexString.append(hex);
                }
                // Converts it to String so it can be returned
                System.out.println(hexString.toString());
                System.out.println(hexString.toString());
                System.out.println(hexString.toString());
//                return hexString.toString();
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }


        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}

