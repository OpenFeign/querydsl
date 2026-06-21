package fluentq.jpa;

import org.eclipse.persistence.security.Securable;

/**
 * No-op EclipseLink password encryptor so the external-database test persistence units can use
 * plain-text JDBC passwords.
 *
 * <p>EclipseLink 5.0 attempts to decrypt every configured JDBC password and aborts with {@code
 * EclipseLink-7360} ("Database password was encrypted by deprecated algorithm") when the value is
 * not a valid encrypted token. Registering this encryptor via {@code eclipselink.login.encryptor}
 * makes EclipseLink treat the password verbatim.
 */
public class PlaintextEncryptor implements Securable {

  @Override
  public String encryptPassword(String pswd) {
    return pswd;
  }

  @Override
  public String decryptPassword(String encryptedPswd) {
    return encryptedPswd;
  }
}
