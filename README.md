Certificate pinning in Android
=================

An example project that demonstrates how to pin certificates to a default Apache HTTP client that is shipped with Android.

There are three steps in the process: 

1. Obtain a certificate for the desired host
2. Make sure certificate is in .bks format
3. Pin the certificate to an instance of **DefaultHttpClient**

## Obtaining a .pem certificate for a site

One way to do it is from Firefox browser. The method is described <a href="http://superuser.com/a/97203">here</a>. This  will store the entire certificate chain for the host and it is the recommended method.

Another way is using OpenSSL command line tool. The following command retreives certificate for *api.github.com*:

```shell
openssl s_client -showcerts -connect api.github.com:443 </dev/null 2>/dev/null|openssl x509 -outform PEM >mycertfile.pem
```

Replace *api.github.com* with the hostname of the host you wish to retreive certificate for. Certificate will be stored in *mycertfile.pem* in current directory. You can replace that with the file you desire. However, using this command will only fetch the certificate for the host itself not the whole certificate chain. This will still work, but for additional security use a method that will extract the whole certificate chain.

More info on OpenSSL can be found on: https://www.openssl.org/
 
## Converting to a .bks keystore

After you've obtained a certificate it will usualy be in .pem or .cert format. In order to convert it to .bks format you will first need to obtain a .jar that contains *Bouncy Castle* implementation. A version that has been tested can be downloaded from: Download bouncycastle JAR from http://repo2.maven.org/maven2/org/bouncycastle/bcprov-ext-jdk15on/1.46/bcprov-ext-jdk15on-1.46.jar

In order to convert use the following command: 

```shell
keytool -importcert -v -trustcacerts -file "mycertfile.pem" -alias ca -keystore "keystore.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass test
```

Use -file argument to specify .pem, .cert or .crt certificate file. Output keystore is specified usting -keystore argument. Path to Bouncy Castle .jar must be provided with -providerpath argument. Finally password for the generated keystore is set with -storepass argument.

## Pinning the certificate to DefaultHttpClient

Using trial and error it has been established that only .bks keystores can be used for certificate pinning. Keystore file must be placed in **res/raw** folder.

The following snippet demonstrates loading a keystore: 
```java
InputStream in = resources.openRawResource(certificateRawResource);

keyStore = KeyStore.getInstance("BKS");
keyStore.load(resourceStream, password);
```
When creating an instance of DefaultHttpClient that keystore can be used to pin the certificates that it contains by adding a scheme as follows (the code is simplified in order to give you the basic idea): 
```java
HttpParams httpParams = new BasicHttpParams();

SchemeRegistry schemeRegistry = new SchemeRegistry();
schemeRegistry.register(new Scheme("https", new SSLSocketFactory(keyStore), 443));

ThreadSafeClientConnManager clientMan = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

httpClient = new DefaultHttpClient(clientMan, httpParams);
```
The constructed httpClient will only allow requests to a hosts that are signed with the certificates provided in keystore file. 

Some additional information on certificate pinning in Android can be found here: 
http://nelenkov.blogspot.com/2012/12/certificate-pinning-in-android-42.html

## Using Builder classes provided in example

In order to simplify certificate pinning to HTTP client, two builder classes are supplied in example. They can be used to easily create new instances of HTTP client with pinned certificates. 

In order to create a new instance of DefaultHttpClient you can use **HttpClientBuilder** class: 
```java
DefaultHttpClient httpClient = new HttpClientBuilder()
  .setConnectionTimeout(10000) //timeout until a connection is etablished in ms; zero means no timeout
  .setSocketTimeout(60000) //timeout for waiting for data in ms (socket timeout); zero means no timeout
  .setHttpPort(80) //sets the port for HTTP connections, default is 80
  .setHttpsPort(443) //sets the port for HTTPS connections, default is 443
  .setCookieStore(new BasicCookieStore()) //assigns a cookie store, BasicCookieStore is assigned by default
  .pinCertificates(getResources(), R.raw.keystore, STORE_PASS) //pins the certificate from raw resources
  .build();
```

If you are using Retrofit library, you can create DefaultHttpClient that can be used with retrofit by using **RetrofitClientBuilder** class:

```java
Client.Provider client = new RetrofitClientBuilder()
  .pinCertificates(getResources(), R.raw.keystore, STORE_PASS) //pins the certificate from raw resources
  .build();

RestAdapter restAdapter = new RestAdapter.Builder()
  .setServer("https://api.github.com")
  .setClient(client)
  .build();
```

It will build a **Client.Provider** that can be assigned to RestAdapter with setClient() method. For more details check the code and comments in <a href="https://github.com/ikust/hello-pinnedcerts/blob/master/pinnedcerts/src/main/java/co/infinum/https/MainActivity.java">MainActivity.java</a>.
