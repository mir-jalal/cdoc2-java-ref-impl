package ee.cyber.cdoc2.crypto.keymaterial.decrypt;


import java.util.Arrays;
import java.util.Objects;

import ee.cyber.cdoc2.crypto.EncryptionKeyOrigin;
import ee.cyber.cdoc2.crypto.KeyLabelTools;
import ee.cyber.cdoc2.crypto.keymaterial.DecryptionKeyMaterial;


/**
 * Represents key material required for decryption with symmetric key derived from password.
 *
 * @param password password chars
 * @param keyLabel key label
 */
public record PasswordDecryptionKeyMaterial(
    char[] password,
    String keyLabel
) implements DecryptionKeyMaterial {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordDecryptionKeyMaterial that = (PasswordDecryptionKeyMaterial) o;
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
        return "PasswordDecryptionKeyMaterial{"
            + "password=[hidden]"
            + ", keyLabel='" + keyLabel + '}';
    }

    @Override
    public Object getRecipientId() {
        if (KeyLabelTools.isFormatted(keyLabel)) {
            return KeyLabelTools.extractKeyLabel(keyLabel);
        }
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
