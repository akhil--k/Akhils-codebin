package com.akhil.generalizedFibonacci;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class FibonacciMadness {

	private static final Scanner scanner = new Scanner(System.in);
	private static Map<Integer, BigInteger> memoFib = new LinkedHashMap<>();
	public static void main(String[] args) {

		int t = scanner.nextInt();

		for(int i = 0 ; i < t; i++) {
			int p  = scanner.nextInt();
			int fp = scanner.nextInt();
			int q  = scanner.nextInt();
			int fq = scanner.nextInt();
			int r  = scanner.nextInt();
			if(p < q) {
				System.out.println(findFr(p, fp, q, fq, r));
			}else {
				System.out.println(findFr(q, fq, p, fp, r));
			}
			
		}
		memoFib = null; 
		scanner.close();
	}

	private static String findFr(int p, int fp, int q, int fq, int r) {

		BigInteger numerator = new BigInteger(String.valueOf(fq)).subtract(gerenarateFibSeries(q-p).multiply(new BigInteger(String.valueOf(fp))));
		BigInteger denominator = gerenarateFibSeries(q-p-1);
		String x ;
		if(numerator.mod(denominator).equals(BigInteger.ZERO)) {
			x = String.valueOf(numerator.divide(denominator));
		} else {
			x = numerator + "/" + denominator;
			x = minimize(x);
		}
		numerator = null;
		denominator = null;
		if(p-1 == r) return x;
		/***
		*  x = [fq - Fib(q-p) * Fp ]/ Fib(q-p-1)
		*  n = p - r
		*  Fp-n = Math.pow(-1, n-1) * Fib(n-1) * X + Math.pow(-1, n-2) * Fib(n-2) * Fp; if n > 0
		*  Fp-n = Math.pow(-1, 0) * Fib(Math.abs(p-r+1)) * X + Math.pow(-1, 0) * Fib(Math.abs(p-r)) * Fp ; if n < 0
		*
		***/
		BigInteger fibn_1 = gerenarateFibSeries(p - r > 0 ? p-r-1 : Math.abs(p-r+1));
		BigInteger fibn_2 = gerenarateFibSeries(p-r > 0 ? p-r-2 : Math.abs(p-r));
		String fr = performAddSub(addSign(Math.pow(-1,(p - r - 1 >  0 ? p - r - 1:0 )), performMulti(fibn_1.toString(), x)),
				addSign(Math.pow(-1,(p-r-2 > 0 ? p-r-2:0 )), performMulti(fibn_2.toString(), String.valueOf(fp))), true);
		fibn_1 = null;
		fibn_2 = null;
		x = null;
		return minimize(fr);
	}

	private static String minimize(String fr) {
		StringBuilder str = new StringBuilder();
		if(fr.contains("/")) {
			String[] parts = fr.split("/");
			BigInteger a = new BigInteger(parts[0]);
			BigInteger b = new BigInteger(parts[1]);
			parts = null;
			BigInteger gd = a.gcd(b);
			a = a.divide(gd);
			b = b.divide(gd);
			gd = null;
			str.append(a).append("/").append(b);
			a = null;
			b = null;
		}else {
			str.append(fr);
			if(str.length() == 1 && str.indexOf("-") != -1) {
				str = new StringBuilder("0");
			}
		}
		return str.toString();
	}

	private static String performMulti(String a, String x) {
		StringBuilder result = new StringBuilder();
		boolean isNegative = false;
		if(x.contains("-")) {
			isNegative = true;
			x = x.substring(1);
		}
		boolean isANegative = false;
		if(a.contains("-")) {
			isANegative = true;
			a = a.substring(1);
		}
		BigInteger aB = new BigInteger(a);
		if(x.contains("/")) {
			String[] parts = x.split("/");
			BigInteger c = new BigInteger(parts[0]);
			BigInteger d = new BigInteger(parts[1]);
			BigInteger ac = aB.multiply(c);
			c = null;
			if(ac.mod(d).equals(BigInteger.ZERO)) {
				result.append(ac.divide(d));
			}else {
				result.append(ac).append("/").append(d);
			}
			ac = null;
			d = null;
		}else {
			result.append(aB.multiply(new BigInteger(x)));
		}
		aB = null;
		if((isNegative && !isANegative) || (!isNegative && isANegative)) {
			result.append("-").append(result);
		}
		return result.toString();
	}

	private static String addSign(double pow, String num) {
		if(num.contains("-")) {
			num = num.split("-")[1];
			return pow == -1 ? num : "-" + num;
		}
		return pow == -1 ? "-" + num : num;
	}

	//performs addition/subtraction of the given two numbers in string format
	private static String performAddSub(String term1, String term2, boolean isAddition) {
		StringBuilder result = new StringBuilder();
		if(term1.contains("/") || term2.contains("/") ) {
			String[] parts1 = term1.split("/");
			String[] parts2 = term2.split("/");
			BigInteger a = new BigInteger(parts1[0]);
			BigInteger c = new BigInteger(parts2[0]);
			BigInteger d = BigInteger.ONE;
			BigInteger b = BigInteger.ONE;
			if(parts1.length == 2) {
				b = new BigInteger(parts1[1]);
			}
			if(parts2.length == 2) {
				d = new BigInteger(parts2[1]);
			}
			BigInteger ad = a.multiply(d);
			BigInteger bc = b.multiply(c);
			a = null;
			c = null;
			BigInteger numerator = isAddition ? ad.add(bc) : ad.subtract(bc);
			ad = null;
			bc = null;
			BigInteger denominator = b.multiply(d);
			b = null;
			d = null;
			if(numerator.mod(denominator).equals(BigInteger.ZERO)) {
				result.append(numerator.divide(denominator));
			}else {
				result.append(numerator).append("/").append( denominator);
			}
			numerator = null;
			denominator = null;
		}else {
			if(isAddition) {
				result.append(new BigInteger(term1).add(new BigInteger(term2)).toString());
			}else {
				result.append(new BigInteger(term1).subtract(new BigInteger(term2)).toString());
			}
		}
		return result.toString();
	}

	private static BigInteger gerenarateFibSeries(int n) {
		if(memoFib.containsKey(n)) {
			return memoFib.get(n);
		}else if(n < 2) {
			memoFib.put(n, BigInteger.ONE);
			return BigInteger.ONE;
		}else {
			BigInteger val = new BigInteger(performAddSub(gerenarateFibSeries(n-1).toString(), gerenarateFibSeries(n-2).toString(), true));
			memoFib.put(n, val);
			return val;
		}
	}
}