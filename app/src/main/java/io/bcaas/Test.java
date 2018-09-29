package io.bcaas;

import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.decimal.DecimalTool;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas
 * @author: catherine
 * @time: 2018/9/6
 */
public class Test {
    public static void main(String[] args) throws Exception {

//        Gson gson = new GsonBuilder()
//                .disableHtmlEscaping()
//                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
//                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
//                .create();
////        String sha = "{\"previous\":\"fd94326c462568ddf9defb371be939d0e678e35c94eb3a0d8de8b7bbfbfc45ad\",\"blockService\":\"BCC\",\"blockType\":\"Send\",\"blockTxType\":\"Matrix\",\"destination_wallet\":\"1NNvqxh9Wb8HAc1nMdSKMvVXUTjN2jjbe1\",\"balance\":\"98627798\",\"amount\":\"100000\",\"representative\":\"13d9BptSPpZgSy2YPpBYYsarihLNSUByDh\",\"wallet\":\"1PmR1EUzWdygApeuNX5WU9KqdwfEYjzzqp\",\"work\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"date\":\"1537267632659\"}";
//        String sha = "{\"previous\":\"e0dae9de48c52c018ca10a02ba3efad4f7291b3da59b703773257cf57b2451b6\",\"blockService\":\"BCC\",\"blockType\":\"Receive\",\"blockTxType\":\"Matrix\",\"sourceTxhash\":\"85ffffcf7fc703e5866f245fb25b81a9ec3393f416b9277548fea16ae98b0dc5\",\"amount\":\"100000\",\"representative\":\"16Yh75suSWm5fAE13ByNZCGThVLZG6sUho\",\"wallet\":\"1NNvqxh9Wb8HAc1nMdSKMvVXUTjN2jjbe1\",\"work\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"date\":\"1537267668378\"}";
//        try {
////            System.out.println(Sha256Tool.doubleSha256ToString(sha));
//            System.out.println(KeyTool.sign(BcaasApplication.getStringFromSP(Constants.Preference.PRIVATE_KEY), sha));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        String content = "16FvSZybHsb5BedwhVF4trJKj4su8QDxsY";
//        String pre = content.substring(0, 4);
//        String last = content.substring(content.length() - 5, content.length() - 1);
//        String result = pre + "***" + last;
//        System.out.println(result);


//        BigDecimal balance = new BigDecimal("356783196852691111121212331223.876543216489643721231241212333");
////        BigDecimal amount = new BigDecimal("356783196852691111121212331223.111111111111111111111111111111");
////
////        System.out.println(balance.subtract(amount));
////        System.out.println(balance.add(amount));

//        if (DecimalTool.calculateFirstSubtractSecondValue("0", "1").equals(MessageConstants.NO_ENOUGH_BALANCE)) {
//            System.out.println("-");
//        } else {
//            System.out.println("+");
//        }
//
//        System.out.println(DecimalTool.transferDisplay(""));
//        try {
//            System.out.println(DecimalTool.calculateFirstSubtractSecondValue("0", "1"));
//        } catch (Exception e) {
//            System.out.println("ha");
//            e.printStackTrace();
//        } finally {
//            System.out.println("yes");
//        }
        createValues();
        testTime();
    }

    public static void testTime() {
        System.out.println(DateFormatTool.getUTCDateForAMPMFormat(DateFormatTool.getUTCTimeStamp()));

    }

    public static void createValues() {
        for (int i = 1; i < 301; i++) {
            System.out.println("<dimen name=\"d" + i + "\">" + i / 1.5 + "dp</dimen>");
        }
        for (int i = 5; i < 40; i++) {
            System.out.println("<dimen name=\"text_size_" + i + "\">" + i / 1.5 + "sp</dimen>");
        }
    }
}
