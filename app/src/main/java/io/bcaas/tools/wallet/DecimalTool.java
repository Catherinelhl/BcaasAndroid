package io.bcaas.tools.wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalTool {

	/**
	 * 第一個參數 - 第二個參數 ＝ 回傳的數值  
	 * <br>
	 * FirstValue不能小於SecondValue
	 * @param firstValue
	 * @param secondValue
	 */
	public static String calculateFirstSubtractSecondValue(String firstValue, String secondValue) throws Exception {

		DecimalFormat decimalFormat = new DecimalFormat("0.00000000");

		// 計算小數八位，第九位無條件捨去
		BigDecimal bigDecimalFirstValue = new BigDecimal(firstValue).setScale(8, RoundingMode.FLOOR);
		BigDecimal bigDecimalSecondValue = new BigDecimal(secondValue).setScale(8, RoundingMode.FLOOR);
		
		// FirstValue不能小於SecondValue
		if(bigDecimalFirstValue.compareTo(bigDecimalSecondValue) == -1) {
			throw new Exception();
		}
		
		BigDecimal bigDecimalNum = bigDecimalFirstValue.subtract(bigDecimalSecondValue);

		String num = decimalFormat.format(bigDecimalNum);

		return num;
	}

	/**
	 * 一般帳戶金額計算，總發行量 - 獎勵金額 = 一般帳戶的金額
	 * @param circulation
	 * @param coinBase
	 */
	public static String generalAccountCoin(String circulation, String coinBase) throws Exception {

		DecimalFormat decimalFormat = new DecimalFormat("0.00000000");

		// 計算小數八位，第九位無條件捨去
		BigDecimal bigDecimalCirculation = new BigDecimal(circulation).setScale(8, RoundingMode.FLOOR);
		BigDecimal bigDecimalCoinBase = new BigDecimal(coinBase).setScale(8, RoundingMode.FLOOR);
		
		// CoinBase不能小於總發行量
		if(bigDecimalCirculation.compareTo(bigDecimalCoinBase) == -1) {
			throw new Exception();
		}
		
		BigDecimal bigDecimalNum = bigDecimalCirculation.subtract(bigDecimalCoinBase);

		String num = decimalFormat.format(bigDecimalNum);

		return num;
	}
	
	/**
	 * 轉換成顯示每三位數加逗號，小數只顯示八位
	 * @param decimal
	 */
	public static String transferDisplay(String decimal) {

		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00000000");

		return decimalFormat.format(decimal);
	}

	
	
	
	
	
	
	public static void main(String[] args) {

		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00000000");
		
		BigDecimal bigDecimal = new BigDecimal("99999.10006080").setScale(8, RoundingMode.FLOOR);
		System.out.println(decimalFormat.format(bigDecimal));
		
//		BigDecimal bigDecimalCirculation = new BigDecimal("300000000000.0000000168").setScale(8, RoundingMode.FLOOR);
//		BigDecimal bigDecimalCoinBase = new BigDecimal("200000000000.0000000168").setScale(8, RoundingMode.FLOOR);
//		
//		if(bigDecimalCirculation.compareTo(bigDecimalCoinBase) == 1) {
//			System.out.println(bigDecimalCirculation.compareTo(bigDecimalCoinBase));
//		} else if(bigDecimalCirculation.compareTo(bigDecimalCoinBase) == -1) {
//			System.out.println(bigDecimalCirculation.compareTo(bigDecimalCoinBase));
//		} else if(bigDecimalCirculation.compareTo(bigDecimalCoinBase) == 0) {
//			System.out.println(bigDecimalCirculation.compareTo(bigDecimalCoinBase));
//		}
		
//		String num = decimalFormat.format(bigDecimalCirculation.subtract(bigDecimalCoinBase));
//
//		System.out.println(num);

//		BigDecimal bigDecimalNum = new BigDecimal(num).setScale(8, RoundingMode.FLOOR);
//		BigDecimal bigDecimal2 = new BigDecimal("10000000000.00000002").setScale(8, RoundingMode.FLOOR);
//
//		System.out.println(bigDecimalNum.subtract(bigDecimal2));

//		BigDecimal scaled = bigDecimalCirculation.setScale(8, RoundingMode.FLOOR);
//		System.out.println(bigDecimalCirculation + " -> " + scaled);
//
//		BigDecimal rounded = bigDecimalCoinBase.round(new MathContext(3, RoundingMode.FLOOR));
//		System.out.println(bigDecimalCoinBase + " -> " + rounded);

	}

}
