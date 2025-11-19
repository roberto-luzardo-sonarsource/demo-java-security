/*
 * Copyright (C) 2025 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package demo.security.util;

import java.math.BigInteger;
import java.util.Random;
import java.util.logging.Logger;

public class RSAKeyGeneratorUtil {

  private static final Logger LOGGER = Logger.getLogger(RSAKeyGeneratorUtil.class.getName());
  private static final int DEFAULT_KEY_SIZE = 1024;
  private static final int PRIME_CERTAINTY = 20;
  private static final int EXPONENT_INCREMENT = 2;
  private static final String PUBLIC_EXPONENT_VALUE = "65537";

  private BigInteger p;
  private BigInteger q;
  private BigInteger n;
  private BigInteger phi;
  private BigInteger e;
  private BigInteger d;
  private int bitLength;
  private Random random;

  public RSAKeyGeneratorUtil(int bitLength) {
    this.bitLength = bitLength;
    this.random = new Random();
    generateKeys();
  }

  private void generateKeys() {
    p = generatePrime(bitLength / EXPONENT_INCREMENT, random);
    q = generatePrime(bitLength / EXPONENT_INCREMENT, random);
    while (p.equals(q)) {
      q = generatePrime(bitLength / EXPONENT_INCREMENT, random);
    }
    n = p.multiply(q);
    phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

    e = new BigInteger(PUBLIC_EXPONENT_VALUE);
    while (!phi.gcd(e).equals(BigInteger.ONE)) {
      e = e.add(BigInteger.valueOf(EXPONENT_INCREMENT));
    }

    d = e.modInverse(phi);
  }

  private static BigInteger generatePrime(int bitLength, Random rnd) {
    BigInteger prime;
    do {
      prime = new BigInteger(bitLength, rnd).setBit(0);
    }
    while (!prime.isProbablePrime(PRIME_CERTAINTY));
    return prime;
  }

  public BigInteger getModulus() {
    return n;
  }

  public BigInteger getPublicExponent() {
    return e;
  }

  public BigInteger getPrivateExponent() {
    return d;
  }

  public BigInteger getP() {
    return p;
  }

  public BigInteger getQ() {
    return q;
  }

  public static void main(String[] args) {
    var rsa = new RSAKeyGeneratorUtil(DEFAULT_KEY_SIZE);
    LOGGER.info("RSA Public Key:");
    LOGGER.info("Modulus (n): " + rsa.getModulus());
    LOGGER.info("Public Exponent (e): " + rsa.getPublicExponent());
    LOGGER.info("\nRSA Private Key:");
    LOGGER.info("Private Exponent (d): " + rsa.getPrivateExponent());
  }
}
