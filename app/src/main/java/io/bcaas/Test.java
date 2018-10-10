package io.bcaas;

import java.math.BigDecimal;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.DateFormatTool;
import io.bcaas.tools.decimal.DecimalTool;
import io.bcaas.tools.gson.GsonTool;
import io.bcaas.vo.ClientIpInfoVO;

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


//        createValues();
//        testTime();
//        testTCPConnect();
//        testUpdate();
//        testCatchException();
        String info1="04b25c107991095b952546df247d3032503f457118f030d151c4c4be820b8f7c0d7b05279bcc2e812f275fc4633e96642bb8a1fdea90299374a0f9d017fb5998ac";
        String info2="04b25c107991095b952546df247d3032503f457118f030d151c4c4be820b8f7c0d7b05279bcc2e812f275fc4633e96642bb8a1fdea90299374a0f9d017fb5998ac";

        System.out.println(info1.equals(info2));
    }

    private static void testBlanceAdd() throws Exception {
        BigDecimal balance = new BigDecimal("356783196852691111121212331223.876543216489643721231241212333");
        BigDecimal amount = new BigDecimal("356783196852691111121212331223.111111111111111111111111111111");

        System.out.println(balance.subtract(amount));
        System.out.println(balance.add(amount));

        if (DecimalTool.calculateFirstSubtractSecondValue("0", "1").equals(MessageConstants.NO_ENOUGH_BALANCE)) {
            System.out.println("-");
        } else {
            System.out.println("+");
        }
    }

    private static void testCatchException() {

        System.out.println(DecimalTool.transferDisplay(""));
        try {
            System.out.println(DecimalTool.calculateFirstSubtractSecondValue("0", "1"));
        } catch (Exception e) {
            System.out.println("ha");
            e.printStackTrace();
        } finally {
            System.out.println("yes");
        }

        System.out.println("go ....");
    }

    private static void testTCPConnect() {

        String content = "{\"macAddressExternalIp\":\"ab61c77b6dcc94ec2f7c24bc6367dd5a0991f48c40ed4d33a810c332d37695bc\",\"externalIp\":\"140.206.56.118\",\"internalIp\":\"192.168.31.5\",\"clientType\":\"AuthNode\",\"externalPort\":45261,\"internalPort\":63068,\"virtualCoin\":[{\"BCC\":\"BCC\",\"COS\":\"COS\"}],\"rpcPort\":54964,\"internalRpcPort\":43802,\"walletAddress\":\"1HdRhxdydbhkZtBgrZpJQsm9eKDbksFDi1\"}";
        ClientIpInfoVO clientIpInfoVO = GsonTool.getGson().fromJson(content, ClientIpInfoVO.class);
        System.out.println(clientIpInfoVO);
    }

    public static void testUpdate() {
        String currentVersionName = "0.0.1";
        //1:解析当前本地的版本信息
        String[] localVersionSplit = currentVersionName.split("\\.");
        //2:解析服务器传回的版本信息
        String[] serverVersionSplit = "1.0.0".split("\\.");
        //3:比较两者是否相等，如果服务器的大于本地的，那么需要提示更新
        System.out.println(localVersionSplit.length);
        System.out.println(serverVersionSplit.length);
        if (localVersionSplit.length < 3) {
            System.out.println("1");
            return;
        }
        if (serverVersionSplit.length < 3) {
            System.out.println("2");
            return;
        }
        if (Integer.valueOf(localVersionSplit[0]) < Integer.valueOf(serverVersionSplit[0])) {
            System.out.println("3");
            return;
        }
        if (Integer.valueOf(localVersionSplit[1]) < Integer.valueOf(serverVersionSplit[1])) {
            System.out.println("4");
            return;
        }
        if (Integer.valueOf(localVersionSplit[2]) < Integer.valueOf(serverVersionSplit[2])) {
            System.out.println("5");
            return;
        }
        String content = "{\"macAddressExternalIp\":\"9fbf06514df2db2a630cb236c9ecdf573568d966841b9c4aaa0a7d0844be8d73\",\"externalIp\":\"180.173.154.107\",\"internalIp\":\"192.168.31.32\",\"clientType\":\"AuthNode\",\"externalPort\":5450,\"internalPort\":7521,\"virtualCoin\":[{\"BCC\":\"390662eec397b1136ff0364250254f2941bd0fec6916a0328c1806d283696b83\"}],\"rpcPort\":64859,\"internalRpcPort\":38666,\"walletAddress\":\"17K2B2ExMMjksNDBbx74u8ttGakTg8MeB7\"}";
        ClientIpInfoVO clientIpInfoVO = GsonTool.getGson().fromJson(content, ClientIpInfoVO.class);
        System.out.println(clientIpInfoVO);
    }

    public static void testTime() {
        for (int i = 0; i < 2; i++) {
            try {
                try {
                    System.out.println(DateFormatTool.getUTCDateForAMPMFormat("如果"));
                    System.out.println(DateFormatTool.getUTCDateTransferCurrentTimeZone("-1"));
                } catch (Exception e) {
                    System.out.println(i + "cath==>" + e.getCause());
                    break;
                } finally {
                    System.out.println(i + "finally");
                }
            } catch (Exception e) {
                System.out.println(i + "1cath==>" + e.getCause());
                break;
            } finally {
                System.out.println(i + "1finally");
            }
        }

    }

    public static void createValues() {
        for (int i = 1; i < 301; i++) {
            System.out.println("<dimen name=\"d" + i + "\">" + i * 1.4 + "dp</dimen>");
        }
        for (int i = 5; i < 40; i++) {
            System.out.println("<dimen name=\"text_size_" + i + "\">" + i * 1.4 + "sp</dimen>");
        }
    }
}
