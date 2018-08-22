package io.bcaas.bean;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.gson.WalletRequestJson;
import io.bcaas.vo.WalletVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public class test {
    public static void main(String[] args) {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        String responseJson = "{\"walletVO\":{\"walletAddress\":\"1DmpeQtAmdhiUyUujxiqPVGUfUmCZFuEUC\",\"accessToken\":\"MURtcGVRdEFtZGhpVXlVdWp4aXFQVkdVZlVtQ1pGdUVVQzE1MzQ3MzM3MzQxOTQ\\u003d\",\"blockService\":\"BCC\",\"clientIpInfoVO\":{\"macAddressExternalIp\":\"ab61c77b6dcc94ec2f7c24bc6367dd5a0991f48c40ed4d33a810c332d37695bc\",\"externalIp\":\"140.206.56.118\",\"internalIp\":\"192.168.31.5\",\"clientType\":\"AuthNode\",\"externalPort\":43575,\"internalPort\":65188,\"virtualCoin\":[{\"BCC\":\"BCC\"}],\"rpcPort\":34847},\"seedFullNodeList\":[{\"ip\":\"127.0.0.1\",\"port\":80,\"rpcPort\":20000,\"deviceLevel\":0},{\"ip\":\"192.168.31.22\",\"port\":9002,\"rpcPort\":20002,\"deviceLevel\":1},{\"ip\":\"192.168.31.23\",\"port\":9003,\"rpcPort\":20003,\"deviceLevel\":1},{\"ip\":\"192.168.31.24\",\"port\":9004,\"rpcPort\":20004,\"deviceLevel\":1},{\"ip\":\"192.168.31.25\",\"port\":9005,\"rpcPort\":20005,\"deviceLevel\":1}]},\"success\":true,\"code\":200,\"message\":\"Login Success.\",\"size\":0}";

        WalletVO walletVO = gson.fromJson(responseJson, WalletVO.class);

        System.out.println(walletVO);

        System.out.println(walletVO.getAccessToken());
        String blockService = "";

        WalletRequestJson walletRequestJson = new WalletRequestJson(walletVO.getAccessToken(), walletVO.getWalletAddress(), blockService);


    }
}
