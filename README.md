Certificate pinning in Android
=================

An example project that demonstrates how to pin certificates to a default Apache HTTP client that is shipped with Android.

There are three steps in the process: 

1. Obtain a certificate for the desired host
2. Make sure certificate is in .bks format
3. Pin the certificate to an instance of **DefaultHttpClient**

## Obtaining a .pem certificate for a site

The easiest way to do it is using OpenSSL command line tool. The following command retreives certificate for *api.github.com*:

```shell
openssl s_client -showcerts -connect api.github.com:443 </dev/null 2>/dev/null|openssl x509 -outform PEM >mycertfile.pem
```

Replace *api.github.com* with the hostname of the host you wish to retreive certificate for. Certificate will be stored in *mycertfile.pem* in current directory. You can replace that with the file you desire.

More info on OpenSSL can be found on: https://www.openssl.org/
 
## Converting to a .bks keystore

After you've obtained a certificate it will usualy be in .pem or .cert format. In order to convert it to .bks format you will first need to obtain a .jar that contains *Bouncy Castle* implementation. A version that has been tested can be downloaded from: Download bouncycastle JAR from http://repo2.maven.org/maven2/org/bouncycastle/bcprov-ext-jdk15on/1.46/bcprov-ext-jdk15on-1.46.jar

In order to convert use the following command: 

```shell
keytool -importcert -v -trustcacerts -file "mycertfile.pem" -alias ca -keystore "keystore.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass test  
```

Use -file argument to specify .pem, .cert or .crt certificate file. Output keystore is specified usting -keystore argument. Path to Bouncy Castle .jar must be provided with -providerpath argument. Finally password for the generated keystore is set with -storepass argument.

## Pinning the certificate to DefaultHttpClient
