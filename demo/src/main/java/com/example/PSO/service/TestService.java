package com.example.PSO.service;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.*;
import org.asynchttpclient.Response;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TestService {

    @Value("${vapid.private.key}")
    private String privateKey;
    @Value("${vapid.public.key}")
    private String publicKey;
    private PushAsyncService pushService;
    private List<Subscription> subscriptions = new ArrayList<>();

    @PostConstruct
    private void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        if (publicKey == null || privateKey == null) {
            ECNamedCurveParameterSpec parameterSpec =
                    ECNamedCurveTable.getParameterSpec("secp256r1");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
            keyPairGenerator.initialize(parameterSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

            byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
            byte[] privateKeyBytes = privateKey.getD().toByteArray();

            this.publicKey = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKeyBytes);
            this.privateKey = Base64.getUrlEncoder().withoutPadding().encodeToString(privateKeyBytes);
        }
        System.out.println("private key " + this.privateKey);
        System.out.println("public key " + this.publicKey);

        pushService = new PushAsyncService(this.publicKey, this.privateKey, "mailto:paolo6.bursic6@gmail.com");
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void subscribe(Subscription subscription) {
        System.out.println("Subscribed to " + subscription.endpoint);
        this.subscriptions.add(subscription);
    }

    public void unsubscribe(String endpoint) {
        System.out.println("Unsubscribed from " + endpoint);
        subscriptions = subscriptions.stream().filter(s -> !endpoint.equals(s.endpoint))
                .collect(Collectors.toList());
    }

    public void sendNotification(Subscription subscription, String messageJson) {
        try {
            System.out.println("pub key inside " + this.publicKey);
            CompletableFuture<Response> responsePromise = pushService.send(new Notification(subscription, messageJson));
            Response response = responsePromise.get();
            String message = response.getResponseBody();
            System.out.println(message);

        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException |
                 InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendNotifications(String message) {
        System.out.println("Sending notifications to all subscribers");

        subscriptions.forEach(subscription -> {
            sendNotification(subscription, message);
        });
    }

    public void unsubscribeAll() {
        System.out.println("Unsubscribing from all endpoints");
        subscriptions.clear();
    }

}
