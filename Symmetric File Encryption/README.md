Name: Ruben Vasconcelos   
Programme: CASE4  
Module Code: CA4005  
Assignment Title: Symmetric File Encryption Using Password and Salt  
Submission Date: 08-Nov-2015  


The aim of this assignment is to perform symmetric encryption of a file using the block cipher AES where the key is derived from a password and a salt.  

The password you use should be encoded using UTF-8 and be considered to be "strong". The salt will be a randomly generated 128-bit value. The password (p) and salt (s) will be concatenated together (p||s) and hashed 200 times using SHA-256. The resulting digest (H200(p||s)) will then be used as your 256-bit AES key (k).  

You will then encrypt an input binary file using AES in CBC mode with the 256-bit key k and a block size of 128-bits. The IV for this encryption (i) will be a randomly generated 128-bit value. You will use the following padding scheme (as given in lectures): if the final part of the message is less than the block size, append a 1-bit and fill the rest of the block with 0-bits; if the final part of the message is equal to the block size, then create an extra block starting with a 1-bit and fill the rest of the block with 0-bits.  

You will then encrypt your password using RSA with the encryption exponent (e) and my public modulus (N). This is done by calculating pe (mod N). This will use your own implementation of modular exponentiation rather than a library method for this purpose. For the purpose of this assignment no padding should be added to the password before encryption (although this would normally be done).  

The encryption exponent (e) is 65537 and my public modulus (N) has the following value (in hexadecimal):  

c406136c 12640a66 5900a9df 4df63a84 fc855927 b729a3a1 06fb3f37 9e8e4190  
ebba442f 67b93402 e535b18a 5777e649 0e67dbee 954bb021 75e43b64 81e7563d  
3f9ff338 f07950d1 553ee6c3 43d3f814 8f71b4d2 df8da7ef b39f846a c07c8652  
01fbb35e a4d71dc5 f858d9d4 1aaa856d 50dc2d27 32582f80 e7d38c32 aba87ba9  

The implementation language must be Java. You will have to make use of the BigInteger class (java.math.BigInteger), the security libraries (java.security.*) and the crypto libraries (javax.crypto.*). You must not make use of the modular exponentiation method provided by the BigInteger class; all modular exponentiation must be done using one of the two methods described in the lectures. You can however make use of the security and crypto libraries to perform the AES encryption and the SHA-256 hashing.  

Once your implementation is complete, you should create a zip file containing all your code, encrypt this file as described above.  

