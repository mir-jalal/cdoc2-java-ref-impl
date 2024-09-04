package ee.cyber.cdoc2.crypto.keymaterial.encrypt;


import java.util.Arrays;
import java.util.Objects;

import ee.cyber.cdoc2.crypto.EncryptionKeyOrigin;
import ee.cyber.cdoc2.crypto.keymaterial.EncryptionKeyMaterial;
import ee.cyber.cdoc2.util.PasswordValidationUtil;


/**
 * Represents key material required for encryption with symmetric key derived from the password.
 *
 * @param password password chars
 * @param keyLabel key label
 */
public record PasswordEncryptionKeyMaterial(
    char[] password,
    String keyLabel
) implements EncryptionKeyMaterial {

    public PasswordEncryptionKeyMaterial {
        PasswordValidationUtil.validatePassword(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordEncryptionKeyMaterial that = (PasswordEncryptionKeyMaterial) o;
        return Arrays.equals(password, that.password)
            && Objects.equals(keyLabel, that.keyLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            Arrays.hashCode(password),
            keyLabel
        );
    }

    @Override
    @SuppressWarnings("java:S2068")
    public String toString() {
        return "PasswordEncryptionKeyMaterial{"
            + "password=[hidden]"
            + ", keyLabel='" + keyLabel + '}';
    }

    @Override
    public String getLabel() {
        return keyLabel;
    }

    @Override
    public EncryptionKeyOrigin getKeyOrigin() {
        return EncryptionKeyOrigin.PASSWORD;
    }

    /**
     * @return password chars to derive the key from
     */
    public char[] getPassword() {
        return this.password;
    }

}
