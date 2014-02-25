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

Replace *api.github.com* with the hostname of the host you wish to retreive certificate for. Certificate will be stored in *mycertfile.pem* in current directory. You can replace that with the directory you desire.

More info on OpenSSL can be found on: https://www.openssl.org/
 
## Converting to a .bks keystore
