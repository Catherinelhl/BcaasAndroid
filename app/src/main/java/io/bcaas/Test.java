package io.bcaas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import io.bcaas.gson.jsonTypeAdapter.GenesisVOTypeAdapter;
import io.bcaas.gson.jsonTypeAdapter.TransactionChainVOTypeAdapter;
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
    public static void main(String[] args) {

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(GenesisVO.class, new GenesisVOTypeAdapter())//初始块有序
                .registerTypeAdapter(TransactionChainVO.class, new TransactionChainVOTypeAdapter())
                .create();
        String sha = " {\"previous\":\"1fab0282805a6f4d2dfb3e856f3d7634d5c07c4ca0d4dc296f66767e04ab7f60\",\"blockService\":\"BCC\",\"blockType\":\"Send\",\"blockTxType\":\"Matrix\",\"destination_wallet\":\"1Fbi26hkDDBfa2Crc5fSrVRRzibhjCUTvN\",\"balance\":\"1699348\",\"amount\":\"1\",\"representative\":\"1AymAWB7Pt2RWELYHFTMxzJ5bDS8DoJJTC\",\"wallet\":\"1AymAWB7Pt2RWELYHFTMxzJ5bDS8DoJJTC\",\"work\":\"0\",\"date\":\"1537007233620\"}";
        try {
            System.out.println(Sha256Tool.doubleSha256ToString(gson.toJson(sha)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        String content = "16FvSZybHsb5BedwhVF4trJKj4su8QDxsY";
//        String pre = content.substring(0, 4);
//        String last = content.substring(content.length() - 5, content.length() - 1);
//        String result = pre + "***" + last;
//        System.out.println(result);


        BigDecimal balance = new BigDecimal("356783196852691111121212331223.876543216489643721231241212333");
        BigDecimal amount = new BigDecimal("356783196852691111121212331223.111111111111111111111111111111");

        System.out.println(balance.subtract(amount));
        System.out.println(balance.add(amount));
    }
}
