package io.bcaas.tools.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.StringTool;

public class DecimalTool {
    private static String TAG = DecimalTool.class.getSimpleName();

    /**
     * 第一個參數 - 第二個參數 ＝ 回傳的數值
     * <br>
     * FirstValue不能小於SecondValue
     *
     * @param firstValue
     * @param secondValue
     */
    public static String calculateFirstSubtractSecondValue(String firstValue, String secondValue) throws Exception {

        DecimalFormat decimalFormat = new DecimalFormat("0.00000000");

        // 計算小數八位，第九位無條件捨去
        BigDecimal bigDecimalFirstValue = new BigDecimal(firstValue).setScale(8, RoundingMode.FLOOR);
        BigDecimal bigDecimalSecondValue = new BigDecimal(secondValue).setScale(8, RoundingMode.FLOOR);

        // FirstValue不能小於SecondValue
        if (bigDecimalFirstValue.compareTo(bigDecimalSecondValue) == -1) {
            return MessageConstants.NO_ENOUGH_BALANCE;
        }

        BigDecimal bigDecimalNum = bigDecimalFirstValue.subtract(bigDecimalSecondValue);

        String num = decimalFormat.format(bigDecimalNum);

        return num;
    }

    /**
     * 轉換成顯示每三位數加逗號，小數只顯示八位
     *
     * @param decimal
     */
    public static String transferDisplay(String decimal) {
        if (decimal.equals("")) {
            decimal = "0";
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00000000");

        BigDecimal bigDecimal = new BigDecimal(decimal).setScale(8, RoundingMode.FLOOR);

        return decimalFormat.format(bigDecimal);
    }

    /**
     * 轉換成小數只顯示八位
     *
     * @param decimal
     */
    public static String transferStoreDatabase(String decimal) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00000000");

        BigDecimal bigDecimal = new BigDecimal(decimal).setScale(8, RoundingMode.FLOOR);

        return decimalFormat.format(bigDecimal);
    }


    public static void main(String[] args) {

        System.out.println(transferDisplay("99999.10006080"));
        System.out.println(transferStoreDatabase("99999.10006080"));

//		DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
//
//		BigDecimal bigDecimal = new BigDecimal("99999.10006080").setScale(8, RoundingMode.FLOOR);
//		System.out.println(decimalFormat.format(bigDecimal));

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

    public static String calculateFirstAddSecondValue(String firstValue, String secondValue) {
        if (StringTool.isEmpty(firstValue)) {
            return secondValue;
        }
        if (StringTool.isEmpty(secondValue)) {
            return firstValue;
        }
        BigDecimal receiveAmount = new BigDecimal(firstValue);
        BigDecimal amount = new BigDecimal(secondValue);
        return receiveAmount.add(amount).toPlainString();
    }

}
