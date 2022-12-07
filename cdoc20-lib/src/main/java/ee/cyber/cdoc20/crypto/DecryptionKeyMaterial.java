package ee.cyber.cdoc20.crypto;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Optional;

/**
 * Represents key material required for decryption.
 */
public interface DecryptionKeyMaterial {
    /**
     * Uniquely identifies the recipient. This data is used to find recipients key material from parsed CDOC header
     * * For EC, this is EC pub key.
     * * For RSA, this is RSA pub key
     * * For SymmetricKey, this is keyLabel
     * @return Object that uniquely identifies Recipient
     */
    Object getRecipientId();

    /**
     * KeyPair used by EC and RSA scenario
     * @return
     */
    default Optional<KeyPair> getKeyPair() {
        return Optional.empty();
    }

    /**
     * Symmetric Key used by Symmetric Key scenario
     * @return
     */
    default Optional<SecretKey> getSecretKey() {
        return Optional.empty();
    }

    static DecryptionKeyMaterial fromSecretKey(String label, SecretKey secretKey) {
        return new DecryptionKeyMaterial() {
            @Override
            public Object getRecipientId() {
                return label;
            }

            @Override
            public Optional<SecretKey> getSecretKey() {
                return Optional.of(secretKey);
            }
        };
    }

    static DecryptionKeyMaterial fromKeyPair(KeyPair recipientKeyPair) {
        return new DecryptionKeyMaterial() {
            @Override
            public Object getRecipientId() {
                return recipientKeyPair.getPublic();
            }

            @Override
            public Optional<KeyPair> getKeyPair() {
                return Optional.of(recipientKeyPair);
            }
        };
    }
}
