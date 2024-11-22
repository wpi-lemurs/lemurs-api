/* Copyright (C) 2024 Worcester Polytechnic University */
package edu.wpi.lemurs.api.security.auth.microsoft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import edu.wpi.lemurs.api.services.EnvironmentService;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Gets the public keys for Microsoft authentication and allows for verification with it. */
public class MicrosoftKeyLocator extends LocatorAdapter<Key> {

  private EnvironmentService env;

  /** Creates a MicrosoftKeyLocator with necessary services. */
  public MicrosoftKeyLocator(EnvironmentService env) {
    this.env = env;
  }

  @Override
  public Key locate(ProtectedHeader header) {
    String kid = header.get("kid").toString();

    String url =
        "https://login.microsoftonline.com/"
            + env.getMicrosoftTenantID()
            + "/discovery/v2.0/keys?appId="
            + env.getMicrosoftAppID();
    String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
    HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
    ResponseEntity<String> response;
    try {
      response =
          new RestTemplate().exchange(urlTemplate, HttpMethod.GET, requestEntity, String.class);
    } catch (RestClientException e) {
      throw new BadCredentialsException("");
    }
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new BadCredentialsException("");
    }
    String info = response.getBody();
    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(info).getAsJsonObject();
    } catch (JsonParseException | IllegalStateException e) {
      throw new BadCredentialsException("");
    }

    JsonObject key = null;
    JsonArray array = jsonObject.getAsJsonArray("keys");
    for (JsonElement k : array) {
      JsonObject keyObject = k.getAsJsonObject();
      String kidCheck = keyObject.get("kid").getAsString();
      if (kidCheck.equals(kid)) {
        key = keyObject;
        break;
      }
    }
    if (key == null) {
      throw new BadCredentialsException("No relevant key found.");
    }

    String x5c = key.getAsJsonArray("x5c").get(0).getAsString();
    byte[] decoded = Base64.getDecoder().decode(x5c);
    CertificateFactory certificateFactory;
    X509Certificate x509Certificate;
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
      x509Certificate =
          (X509Certificate)
              certificateFactory.generateCertificate(new ByteArrayInputStream(decoded));
    } catch (CertificateException e) {
      throw new BadCredentialsException("No such algorithm found.");
    }

    return x509Certificate.getPublicKey();
  }
}
