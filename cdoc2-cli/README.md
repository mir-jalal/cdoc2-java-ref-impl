# Building & Running

To run without building, download latest version of cdoc2-cli.jar from https://github.com/orgs/open-eid/packages 

## Building
Run from cdoc2-java-ref-impl parent directory
```
mvn clean package
```

Will create `cdoc2-cli/target/cdoc2-cli-<version>.jar`

## Running
Run from cdoc2-cli directory

Latest help can be seen by running:
```
java -jar target/cdoc2-cli-<version>.jar
```

where `<version>` must be replaced with the latest version built. Example `0.0.12-SNAPSHOT`

Sample generated CDOC2 documents are located at `cdoc2-java-ref-impl/test/testvectors`

Commands for creating and decrypting sample files using cdoc2-cli are in `cdoc2-java-ref-impl/test/generate_documents.sh`


### Encryption
To create:
- Output file `/tmp/mydoc.cdoc`
- with generated private key
- to recipient `keys/bob_pub.pem`
- to encrypt file 'README.md'

```
java -jar target/cdoc2-cli-*.jar create --file /tmp/mydoc.cdoc -p keys/bob_pub.pem README.md
```

### Encryption with server scenario
Server must be running, see cdoc2-capsule-server/README.md for starting the server

To store keys in key server, specify addition `--server` option:

When encrypting for est-eid card, `-r` <id-code> can be used
```
java -jar target/cdoc2-cli-*.jar create --server=config/localhost/localhost.properties -f /tmp/localhost_id-card.cdoc -r 37903130370 README.md
```

Optionally cdoc2-cli also supports encrypting with "soft" key or certificate

Public key (`-p`)
```
java -jar target/cdoc2-cli-*.jar create --server=config/localhost/localhost.properties -f /tmp/localhost.cdoc -p keys/cdoc2client_pub.key README.md
```

Certificate (`-c` option):
```
java -jar target/cdoc2-cli-*.jar create --server=config/localhost/localhost.properties -f /tmp/localhost.cdoc -c keys/cdoc2client-certificate.pem README.md
```

Key capsule expiration date can be requested when adding expiry duration:
```
-exp P365D
```
Default expiration duration will be used if it is not requested by the client. Default and max 
expiration durations are configurable values in put-server and get-server.


### Encryption with symmetric key and password

Generate key with openssl (minimum length 32 bytes):
```
openssl rand -base64 32
`HHeUrHfo+bCZd//gGmEOU2nA5cgQolQ/m18UO/dN1tE=`
```

Base64 encoded keys must be prefixed with 'base64,', so that key becomes "base64,HHeUrHfo+bCZd//gGmEOU2nA5cgQolQ/m18UO/dN1tE="

Encrypt with generated key and label 'label_b64secret':
```
java -jar target/cdoc2-cli-*.jar create --secret "label_b64secret:base64,aejUgxxSQXqiiyrxSGACfMiIRBZq5KjlCwr/xVNY/B0=" -f /tmp/symmetric.cdoc README.md
```

Or secret read from file (so that secret is not exposed through process list)
```
java -jar target/cdoc2-cli-*.jar create @keys/b64secret.option -f /tmp/symmetric.cdoc README.md
```

```
cat keys/b64secret.option --secret "label_b64secret:base64,aejUgxxSQXqiiyrxSGACfMiIRBZq5KjlCwr/xVNY/B0="
```

Or encrypt with password clear text (note, that password also can be encoded to base64 format, as secret):
```
java -jar target/cdoc2-cli-*.jar create --password "passwordlabel:myPlainTextPassword" -f /tmp/password.cdoc README.md
```

Decryption is done with the same label and key used for encryption
```
java -jar target/cdoc2-cli-*.jar decrypt @keys/b64secret.option -f /tmp/symmetric.cdoc -o /tmp
```

Or with the same label and password used for encryption:
```
java -jar target/cdoc2-cli-*.jar decrypt --password "passwordlabel:myPlainTextPassword" -f /tmp/password.cdoc --output /tmp
```

If cdoc2 file contains only one password, then specifying label is not required and label can be omitted:
```
java -jar target/cdoc2-cli-*.jar decrypt --password ":myPlainTextPassword" -f /tmp/password.cdoc --output /tmp
```


Or with the same label and secret used for encryption:
```
java -jar target/cdoc2-cli-*.jar decrypt --secret "label_b64secret:base64,aejUgxxSQXqiiyrxSGACfMiIRBZq5KjlCwr/xVNY/B0=" -f /tmp/symmetric.cdoc --output /tmp
```

Key and label can be safely stored in a password manager.


### Decryption
To decrypt:
- CDOC2 file `/tmp/mydoc.cdoc`
- with decryption private EC key `keys/bob.pem`
- to output directory `/tmp`

```
java -jar target/cdoc2-cli-*.jar decrypt --file /tmp/mydoc.cdoc -k keys/bob.pem --output /tmp
```

### Decrypting with server scenario
Server must be running, see cdoc2-capsule-server/README.md for starting the server

To decrypt CDOC document that has its keys distributed through key server, cdoc-cli must have `--server` option:

Configuration for id-card (certificate for mutual TLS and private key is read from smart-card)
```
java -jar target/cdoc2-cli-*.jar decrypt --server=config/localhost/localhost.properties -f /tmp/localhost_id-card.cdoc -o /tmp/
```

It is also possible to decrypt documents created with "soft" keys, but configuration for mutual TLS (properties file) and
key (read separately from a file) must match. Also, server must be configured to trust client certificate used for
mutual TLS.
```
java -jar target/cdoc2-cli-*.jar decrypt --server=config/localhost/localhost_pkcs12.properties -f /tmp/localhost.cdoc -k keys/cdoc2client_priv.key -o /tmp/
```


### Re-encryption with password for long time storage

First encrypt the document:
```
java -jar target/cdoc2-cli-*.jar create --secret "mylongpasswd:longstringthatIcanremember,butothersdon'tknow" -f /tmp/symmetric.cdoc README.md
```

Create different directory for re-encrypted container:
```
mkdir -p /tmp/cdoc2
```

Then re-encrypt it with password for long-term storage:
```
java -jar target/cdoc2-cli-*.jar re-encrypt --encpassword "passwordlabel:myPlainTextPassword" --secret "mylongpasswd:longstringthatIcanremember,butothersdon'tknow" -f /tmp/symmetric.cdoc --output /tmp/cdoc2
```

For testing decryption ensure the correct re-encrypted container location:
```
java -jar target/cdoc2-cli-*.jar decrypt --password "passwordlabel:myPlainTextPassword" -f /tmp/cdoc2/symmetric.cdoc --output /tmp/cdoc2
```

### List

```
java -jar target/cdoc2-cli-*.jar list --file /tmp/mydoc.cdoc -k keys/bob.pem
```

or with server scenario:

```
java -jar target/cdoc2-cli-*.jar list --server=config/localhost/localhost_pkcs12.properties -f /tmp/localhost.cdoc -k keys/cdoc2client_priv.key
```

or with password:
```
java -jar target/cdoc2-cli-*.jar list --file /tmp/symmetric.cdoc --password "passwordlabel:myPlainTextPassword"
```

or with secret:
```
java -jar target/cdoc2-cli-*.jar list --file /tmp/symmetric.cdoc --secret "label_b64secret:base64,aejUgxxSQXqiiyrxSGACfMiIRBZq5KjlCwr/xVNY/B0="
```

### List recipients

List recipients. Prints recipient types and key labels from CDOC header.

```
java -jar target/cdoc2-cli-*.jar info -f /tmp/id.cdoc
```


## ID-kaart (Est-id secure card)


### Encrypting for ID-card owner

cdoc2-cli can download authentication certificate (Isikutuvastus PIN1) from SK LDAP directory 
https://www.skidsolutions.eu/repositoorium/ldap/esteid-ldap-kataloogi-kasutamine/

To create cdoc for recipient with id code 37101010021 use:
```
java -jar target/cdoc2-cli-*.jar create --file /tmp/mydoc.cdoc -r 37101010021 README.md
```


### Decrypting with ID-card

To decrypt:
- CDOC file mydoc.cdoc
- use private key from ID-card slot 0 (Isikutuvastus PIN1)
- Decrypt files from cdoc file into current directory
```
java -jar target/cdoc2-cli-*.jar decrypt -f mydoc.cdoc
```

### Certificate extraction

* Run DigiDoc4 client
* Crypto -> Add file (choose random file)
* Recipients -> Certificate from card -> click on certificate -> Show Certificate -> Save

Saved certificate will be .cer file (same as der)

or

* Run DigiDoc4 client
* Crypto -> Add file (choose random file)
* Recipients -> Enter personal code -> Search -> Show Certificate -> Save


### Encrypting documents with certificate

To create:
- Output file `/tmp/mydoc.cdoc`
- with generated private key
- to recipient with certificate `keys/cdoc2client-certificate.pem` (DER or PEM formats are supported)
- to encrypt file 'README.md'

```
java -jar target/cdoc2-cli-*.jar create --file /tmp/mydoc.cdoc -c keys/cdoc2client-certificate.pem README.md
```

Decrypt created container with private key:
```
java -jar target/cdoc2-cli-*.jar decrypt -f /tmp/mydoc.cdoc -k keys/cdoc2client_priv.key --output /tmp
```

### Troubleshooting ID-card

Verify that DigiDoc4 client is running and can access ID-card

cdoc2-cli will try to configure itself automatically. If OpenSC library is installed to non-standard location, then
specify its location by setting 'pkcs11-library' property:

```
java -jar target/cdoc2-cli-*.jar decrypt -Dpkcs11-library=/usr/lib/x86_64-linux-gnu/opensc-pkcs11.so -f mydoc.cdoc
```

More tips for debugging ID-card related issues are provided in cdoc2-lib/pkcs11.README file


## Other configuration options

Set with -D option

```
java -jar target/cdoc2-cli-*.jar decrypt -Dee.cyber.cdoc2.overwrite=false -f mydoc.cdoc
```

#### pkcs11-library
PKCS11 library location. Default is platform specific

Common OpenSC library locations:

* For Windows, it could be C:\Windows\SysWOW64\opensc-pkcs11.dll
* For Linux, it could be /usr/lib/x86_64-linux-gnu/opensc-pkcs11.so
* For OSX, it could be /usr/local/lib/opensc-pkcs11.so

## SafeNet eToken support

Requirements:
* OpenSC is installed
* SafeNet Authentication Client (provides the pkcs11 library) is installed.
  See https://knowledge.digicert.com/generalinformation/INFO1982.html for details.
* Create an OpenSC configuration file `opensc-safenet.cfg` for the USB device in the following format

```
name=SafeNet-eToken
library=/usr/lib/libeToken.so
slot=1
```

To find the slot for the SafeNet eToken, execute:

```
pkcs11-tool --module /usr/lib/libeToken.so -L
```

List entries on the eToken device:

```
keytool -providerclass sun.security.pkcs11.SunPKCS11 -providerarg opensc-safenet.cfg -storetype PKCS11 -storepass YOUR-SAFENET-PIN -list
```

Export Certificate from the SafeNet eToken device:

```
keytool -providerclass sun.security.pkcs11.SunPKCS11 -providerarg opensc-safenet.cfg -storetype PKCS11 -storepass YOUR-SAFENET-PIN -alias YOUR_ENTRY_ALIAS -exportcert -rfc -file etoken-cert.pem
```

Encrypt certificate as described in the "Encrypting documents with certificate" section.

List files encrypted for the eToken device by specifying pkcs11 library, slot and key alias:

```
java -jar target/cdoc2-cli-*.jar list -f file-for-etoken.cdoc -Dpkcs11-library=/usr/lib/libeToken.so -s 2 -a cdoc2-test
```

Decrypt files encrypted for the eToken device by specifying pkcs11 library, slot and key alias:

```
java -jar target/cdoc2-cli-*.jar decrypt -f file-for-etoken.cdoc -Dpkcs11-library=/usr/lib/libeToken.so -s 2 -a cdoc2-test
```

#### ee.cyber.cdoc2.overwrite 
When decrypting, is overwriting files allowed. Default is false

#### ee.cyber.cdoc2.maxDiskUsagePercentage
default 98.0

Decrypting will be stopped if disk usage is over  maxDiskUsagePercentage


#### ee.cyber.cdoc2.tarEntriesThreshold
default 1000

Decrypting will be stopped if container contains over tarEntriesThreshold entries (files)


#### ee.cyber.cdoc2.compressionThreshold
default 10.0

Decrypting will be stopped if compressed file compression ratio is over compressionThreshold

#### ee.cyber.cdoc2.key-label.machine-readable-format.enabled
default true

Key label format can be defined while encrypting. Machine parsable format is enabled by default 
and free text format is allowed if the property disabled.
Machine-readable format is following, where `<data>` is the key label value:
```
data:[<mediatype>][;base64],<data>
```

#### ee.cyber.cdoc2.key-label.file-name.added
default true

Key label `<data>` field contains different parameters. File name is one of them. For security 
purpose it can be hidden in configuration. File name is added by default.
