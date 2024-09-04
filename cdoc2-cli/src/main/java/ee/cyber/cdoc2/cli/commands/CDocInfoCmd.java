package ee.cyber.cdoc2.cli.commands;

import ee.cyber.cdoc2.container.Envelope;
import ee.cyber.cdoc2.container.recipients.PBKDF2Recipient;
import ee.cyber.cdoc2.container.recipients.PublicKeyRecipient;
import ee.cyber.cdoc2.container.recipients.Recipient;
import ee.cyber.cdoc2.container.recipients.ServerRecipient;
import ee.cyber.cdoc2.container.recipients.SymmetricKeyRecipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import static ee.cyber.cdoc2.crypto.KeyLabelTools.extractKeyLabelParams;
import static ee.cyber.cdoc2.crypto.KeyLabelTools.keyLabelParamsForDisplaying;


//S106 Standard outputs should not be used directly to log anything
//CLI needs to interact with standard outputs
@SuppressWarnings("java:S106")
@CommandLine.Command(name = "info",  showAtFileInUsageHelp = true)
public class CDocInfoCmd implements Callable<Void> {

    private static final Logger log = LoggerFactory.getLogger(CDocInfoCmd.class);
    @CommandLine.Option(names = {"-f", "--file" }, required = true,
            paramLabel = "CDOC", description = "the CDOC2 file")
    private File cdocFile;

    // allow -Dkey for setting System properties
    @CommandLine.Option(names = "-D", mapFallbackValue = "", description = "Set Java System property")
    private void setProperty(Map<String, String> props) {
        props.forEach(System::setProperty);
    }

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    @Override
    public Void call() throws Exception {
        List<Recipient> recipients = Envelope.parseHeader(Files.newInputStream(cdocFile.toPath()));
        for (Recipient recipient: recipients) {
            String type = getHumanReadableType(recipient);

            Map<String, String> keyLabelParams
                = extractKeyLabelParams(recipient.getRecipientKeyLabel());

            String server = (recipient instanceof ServerRecipient serverRecipient)
                ? "(server: " + serverRecipient.getKeyServerId() + ")"
                : "";

            System.out.println(
                type + ": " + keyLabelParamsForDisplaying(keyLabelParams) + " " + server
            );
        }

        return null;
    }

    String getHumanReadableType(Recipient recipient) {
        Objects.requireNonNull(recipient); //can't have null recipient, fail with exception

        if (recipient instanceof PublicKeyRecipient publicKeyRecipient) {
            return publicKeyRecipient.getRecipientPubKey().getAlgorithm() + " PublicKey";
        } else if (recipient instanceof SymmetricKeyRecipient) {
            return "SymmetricKey";
        } else if (recipient instanceof PBKDF2Recipient) {
            return "Password";
        } else {
            //unknown recipient type, don't fail as other recipients might be supported
            log.warn("Unknown recipient {}", recipient.getClass());
            return recipient.getClass().toString();
        }
    }
    
}
