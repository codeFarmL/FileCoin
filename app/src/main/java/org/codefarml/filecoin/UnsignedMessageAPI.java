package org.codefarml.filecoin;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:53
 * 文件    FileCoin
 * 描述
 */
class UnsignedMessageAPI {
    private String to;
    private String from;
    private long nonce;
    private String value;
    private long gas_limit;
    private String gasFeeCap;
    private String gasPremium;
    private long method;
    private String params = "";

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGasFeeCap() {
        return gasFeeCap;
    }

    public void setGasFeeCap(String gasFeeCap) {
        this.gasFeeCap = gasFeeCap;
    }

    public String getGasPremium() {
        return gasPremium;
    }

    public void setGasPremium(String gasPremium) {
        this.gasPremium = gasPremium;
    }

    public long getGas_limit() {
        return gas_limit;
    }

    public void setGas_limit(long gas_limit) {
        this.gas_limit = gas_limit;
    }

    public long getMethod() {
        return method;
    }

    public void setMethod(long method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    /**
     *
     * @param json
     * @return
     * pub struct UnsignedMessageAPI {
     *     pub to: String,
     *     pub from: String,
     *     pub nonce: u64,
     *     pub value: String,
     *     #[serde(rename = "gasprice")]
     *     #[serde(alias = "gasPrice")]
     *     #[serde(alias = "gas_price")]
     *     pub gas_price: String,
     *     #[serde(rename = "gaslimit")]
     *     #[serde(alias = "gasLimit")]
     *     #[serde(alias = "gas_limit")]
     *     pub gas_limit: u64,
     *     pub method: u64,
     *     pub params: MessageParams,
     * }
     */

    public static UnsignedMessageAPI from_str(String json){
        UnsignedMessageAPI unsignedMessageAPI = new UnsignedMessageAPI();
        return unsignedMessageAPI;
    }
}
