#PEAPOD with MIX and MATCH

The main() is in the com.client folder in src, within client.java. The com.tools package has all the classes required by the program. In addition to that you need two libraries bcprov and clichhe which are included in the lib folder. Each party for the scheme has a class like Server.java and CertAuth.java, Client.java is a common class for both sender and receiver. the primitives.java class in the com.tools package contains most of the primitives used by the scheme like decryption, encryption, random multiplier gernerator, key pair generator, re-encryption, transformation, blinder, etc.
