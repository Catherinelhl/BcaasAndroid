package io.bcaas.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.NoSuchAlgorithmException;

import io.bcaas.ecc.Sha256Tool;
import io.bcaas.vo.GenesisVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/23
 */
public class Test {

    public static void main(String[] args) {
        String str = "{\"_id\":\"5b7d6ceed4e64b4ba1e9f927\",\"previous\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"blockService\":\"BCC\",\"currencyUnit\":\"BCC\",\"work\":\"0\",\"systemTime\":\"2018-08-22T14:02:21.098Z\"}";
//        String str = " {\n" +
//                "      \"_id\": \"5b7d6ceed4e64b4ba1e9f927\",\n" +
//                "      \"previous\": \"0000000000000000000000000000000000000000000000000000000000000000\",\n" +
//                "      \"blockService\": \"BCC\",\n" +
//                "      \"currencyUnit\": \"BCC\",\n" +
//                "      \"work\": \"0\",\n" +
//                "      \"systemTime\": \"2018-08-22T14:02:21.098Z\"\n" +
//                "    }";
        GenesisVO genesisVO = new GenesisVO();
        genesisVO.set_id("5b7d6ceed4e64b4ba1e9f927");
        genesisVO.setPrevious("0000000000000000000000000000000000000000000000000000000000000000");
        genesisVO.setBlockService("BCC");
        genesisVO.setCurrencyUnit("BCC");
        genesisVO.setWork("0");
        genesisVO.setSystemTime("2018-08-22T14:02:21.098Z");
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        System.out.println(gson.toJson(genesisVO));

        try {
            String s = Sha256Tool.doubleSha256ToString(str);
            System.out.println(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
