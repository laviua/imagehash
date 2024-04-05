# ImageHash 

This repository contains an image hash algorithms.

Zero dependency library

## Requirements
- Java 11 or later

## Features
- Pure kotlin gives ultralight library size

### Perceptual hash feature
- Image resizing with using the Lanczos algorithm
- YCbCr conversion
- Discrete Cosine Transform (DCT)
- Top-left matrix extraction 

## Installation
```
<dependency>
  <groupId>ua.com.lavi.imagehash</groupId>
  <artifactId>imagehash</artifactId>
  <version>$version</version>
</dependency>
```

## Usage

Using plugins is optional, but recommended.

```
val hash1 = Phash.averageHash(ImageIO.read(ByteArrayInputStream(imagebytes)))
val hash2 = Phash.averageHash(ImageIO.read(ByteArrayInputStream(imagebytes2)))

val distance = PhashMatcher.hammingDistance(hash1, hash2)
```
Less value - is more similar


## Maven deployment

Macos install gpg manager
```brew reinstall gnupg```

Generate a new GPG key pair:
Open a terminal (on Linux or macOS) or Command Prompt (on Windows with Gpg4win installed).

Run the following command to start the GPG key generation process:

```gpg --gen-key```

Follow the prompts to enter your name, email address, and an optional comment. Choose a secure passphrase when asked.

After the key pair is generated, you can list your keys using the following command:

```gpg --list-keys```

This will display the public keys in your keyring, including the newly generated one.

Import an existing GPG key pair:

If you have an existing GPG key pair that you want to import into your system, follow these steps:

Locate the files containing your private key (private-key.asc) and public key (public-key.asc).

These are usually ASCII-armored files that start with -----BEGIN PGP PRIVATE KEY BLOCK----- and -----BEGIN PGP PUBLIC KEY BLOCK-----, respectively.

Open a terminal (on Linux or macOS) or Command Prompt (on Windows with Gpg4win installed).

To import the public key, run the following command:

```gpg --import /path/to/public-key.asc```

Replace /path/to/public-key.asc with the actual path to your public key file.

To import the private key, run the following command:

```gpg --allow-secret-key-import --import /path/to/private-key.asc```

Replace /path/to/private-key.asc with the actual path to your private key file.

After importing the key pair, you can list your keys using the following command:

```gpg --list-keys```

This will display the public keys in your keyring, including the imported one.

By following the steps above, you can either generate a new GPG key pair or import an existing one into your system.


GPG key should be placed in ~/.gradle/gradle.properties and has the following format:
```
signing.gnupg.executable=gpg
signing.gnupg.keyName=
signing.gnupg.passphrase=
```

Sonatype keys in the ~/.gradle/gradle.properties should have the following format:
```
sonatypeUsername=
sonatypePassword=
```

How to build and publish to sonatype?
Run the following command in the console:

```
./gradlew clean build publishToMavenLocal
or
./gradlew clean build publishToSonatype
```

### Sonatype publishing
https://central.sonatype.org/pages/ossrh-guide.html
1. open https://oss.sonatype.org/ and login with your credentials
2. Go to the Staging Repositories and close the repository
3. After that, release the repository

### Contributing
To contribute to this project, simply fork the repository and submit a pull request with your changes.

### License
This project is licensed under the Apache 2.0 license. See the LICENSE file for details.
