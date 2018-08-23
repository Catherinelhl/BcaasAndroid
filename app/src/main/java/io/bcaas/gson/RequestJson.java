package io.bcaas.gson;

import io.bcaas.bean.APIKey;
import io.bcaas.vo.*;

import java.util.List;


/**
 * Client 請求使用
 *
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/06/10
 */
public class RequestJson extends ClientRequestJson {

    private VersionVO versionVO;
    private List<VersionVO> versionVOList;

    private ClientIpInfoVO clientIpInfoVO;
    private List<ClientIpInfoVO> clientIpInfoVOList;

    private WalletVO walletVO;

    private DatabaseVO databaseVO;
    private List<DatabaseVO> databaseVOList;

    private PaginationVO paginationVO;
    private List<PaginationVO> paginationVOList;


    private APIKey apiKey;

    // ==================================================================================================
    // constructors
    // ==================================================================================================

    public RequestJson() {
        super();
    }

    public RequestJson(VersionVO versionVO) {
        this.versionVO = versionVO;
    }

    public RequestJson(VersionVO versionVO, List<VersionVO> versionVOList) {
        this.versionVO = versionVO;
        this.versionVOList = versionVOList;
    }
    public RequestJson(ClientIpInfoVO clientIpInfoVO) {
        this.clientIpInfoVO = clientIpInfoVO;
    }

    public RequestJson(ClientIpInfoVO clientIpInfoVO, String methodName) {
        super(methodName);
        this.clientIpInfoVO = clientIpInfoVO;
    }

    public RequestJson(ClientIpInfoVO clientIpInfoVO, List<ClientIpInfoVO> clientIpInfoVOList) {
        super();
        this.clientIpInfoVO = clientIpInfoVO;
        this.clientIpInfoVOList = clientIpInfoVOList;
    }

    public RequestJson(WalletVO walletVO) {
        this.walletVO = walletVO;
    }

    public RequestJson(DatabaseVO databaseVO) {
        super();
        this.databaseVO = databaseVO;
    }

    public RequestJson(DatabaseVO databaseVO, PaginationVO paginationVO) {
        super();
        this.databaseVO = databaseVO;
        this.paginationVO = paginationVO;
    }

    public RequestJson(DatabaseVO databaseVO, List<DatabaseVO> databaseVOList) {
        super();
        this.databaseVO = databaseVO;
        this.databaseVOList = databaseVOList;
    }

    public RequestJson(DatabaseVO databaseVO, List<DatabaseVO> databaseVOList, PaginationVO paginationVO,
                       List<PaginationVO> paginationVOList) {
        super();
        this.databaseVO = databaseVO;
        this.databaseVOList = databaseVOList;
        this.paginationVO = paginationVO;
        this.paginationVOList = paginationVOList;
    }

    public RequestJson(WalletVO walletVO, List<DatabaseVO> databaseVOList, PaginationVO paginationVO,
                       List<PaginationVO> paginationVOList) {
        super();
        this.walletVO = walletVO;
        this.databaseVOList = databaseVOList;
        this.paginationVO = paginationVO;
        this.paginationVOList = paginationVOList;
    }

    public RequestJson(String methodName, APIKey apiKey) {
        super(methodName);
        this.apiKey = apiKey;
    }

    public RequestJson(String methodName, ClientIpInfoVO clientIpInfoVO) {
        super(methodName);
        this.clientIpInfoVO = clientIpInfoVO;
    }

    public RequestJson(PaginationVO paginationVO, List<PaginationVO> paginationVOList) {
        super();
        this.paginationVO = paginationVO;
        this.paginationVOList = paginationVOList;
    }

    // ==================================================================================================
    // getter & setter
    // ==================================================================================================

    public VersionVO getVersionVO() {
        return versionVO;
    }

    public void setVersionVO(VersionVO versionVO) {
        this.versionVO = versionVO;
    }

    public List<VersionVO> getVersionVOList() {
        return versionVOList;
    }

    public void setVersionVOList(List<VersionVO> versionVOList) {
        this.versionVOList = versionVOList;
    }

    public ClientIpInfoVO getClientIpInfoVO() {
        return clientIpInfoVO;
    }

    public void setClientIpInfoVO(ClientIpInfoVO clientIpInfoVO) {
        this.clientIpInfoVO = clientIpInfoVO;
    }

    public List<ClientIpInfoVO> getClientIpInfoVOList() {
        return clientIpInfoVOList;
    }

    public void setClientIpInfoVOList(List<ClientIpInfoVO> clientIpInfoVOList) {
        this.clientIpInfoVOList = clientIpInfoVOList;
    }

    public WalletVO getWalletVO() {
        return walletVO;
    }

    public void setWalletVO(WalletVO walletVO) {
        this.walletVO = walletVO;
    }

    public DatabaseVO getDatabaseVO() {
        return databaseVO;
    }

    public void setDatabaseVO(DatabaseVO databaseVO) {
        this.databaseVO = databaseVO;
    }

    public List<DatabaseVO> getDatabaseVOList() {
        return databaseVOList;
    }

    public void setDatabaseVOList(List<DatabaseVO> databaseVOList) {
        this.databaseVOList = databaseVOList;
    }

    public PaginationVO getPaginationVO() {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO) {
        this.paginationVO = paginationVO;
    }

    public List<PaginationVO> getPaginationVOList() {
        return paginationVOList;
    }

    public void setPaginationVOList(List<PaginationVO> paginationVOList) {
        this.paginationVOList = paginationVOList;
    }

    public APIKey getApiKey() {
        return apiKey;
    }

    public void setApiKey(APIKey apiKey) {
        this.apiKey = apiKey;
    }
}
