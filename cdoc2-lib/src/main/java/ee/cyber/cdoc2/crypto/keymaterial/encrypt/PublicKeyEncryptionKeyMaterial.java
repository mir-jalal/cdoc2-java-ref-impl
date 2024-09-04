package ee.cyber.cdoc2.crypto.keymaterial.encrypt;

import java.security.PublicKey;

import ee.cyber.cdoc2.crypto.EncryptionKeyOrigin;
import ee.cyber.cdoc2.crypto.keymaterial.EncryptionKeyMaterial;


/**
 * Represents key material required for encryption key derived from the public key.
 *
 * @param publicKey public key
 * @param keyLabel key label
 * @param encryptionKeyOrigin encryption key origin
 */
public record PublicKeyEncryptionKeyMaterial(
    PublicKey publicKey,
    String keyLabel,
    EncryptionKeyOrigin encryptionKeyOrigin
) implements EncryptionKeyMaterial {

    @Override
    public String getLabel() {
        return keyLabel;
    }

    @Override
    public EncryptionKeyOrigin getKeyOrigin() {
        return encryptionKeyOrigin;
    }

    /**
     * @return public key to derive the encryption key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

}
