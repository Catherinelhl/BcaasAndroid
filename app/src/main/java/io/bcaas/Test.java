package io.bcaas;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
import io.bcaas.tools.DeviceTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.ecc.KeyTool;
import io.bcaas.tools.ecc.Sha256Tool;
import io.bcaas.vo.GenesisVO;
import io.bcaas.vo.TransactionChainVO;

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
        System.out.println(DecimalTool.calculateFirstAddSecondValue("", "6"));

    }
}
