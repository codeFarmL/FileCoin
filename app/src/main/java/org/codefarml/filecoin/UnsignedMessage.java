package org.codefarml.filecoin;

import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.UnsignedInteger;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:46
 * 文件    FileCoin
 * 描述
 */
public class UnsignedMessage {
    private UnsignedInteger version;
    private ByteString from;
    private ByteString to;
    private UnsignedInteger sequence;
    private ByteString value;
    private ByteString gasFeeCap;
    private ByteString gasPremium;
    private UnsignedInteger gas_limit;
    private UnsignedInteger method_num;
    private ByteString params; //空数组

    public UnsignedInteger getVersion() {
        return version;
    }

    public void setVersion(UnsignedInteger version) {
        this.version = version;
    }

    public ByteString getFrom() {
        return from;
    }

    public void setFrom(ByteString from) {
        this.from = from;
    }

    public ByteString getTo() {
        return to;
    }

    public void setTo(ByteString to) {
        this.to = to;
    }

    public UnsignedInteger getSequence() {
        return sequence;
    }

    public void setSequence(UnsignedInteger sequence) {
        this.sequence = sequence;
    }

    public ByteString getGasFeeCap() {
        return gasFeeCap;
    }

    public void setGasFeeCap(ByteString gasFeeCap) {
        this.gasFeeCap = gasFeeCap;
    }

    public ByteString getGasPremium() {
        return gasPremium;
    }

    public void setGasPremium(ByteString gasPremium) {
        this.gasPremium = gasPremium;
    }

    public ByteString getValue() {
        return value;
    }

    public void setValue(ByteString value) {
        this.value = value;
    }

    public UnsignedInteger getGas_limit() {
        return gas_limit;
    }

    public void setGas_limit(UnsignedInteger gas_limit) {
        this.gas_limit = gas_limit;
    }

    public UnsignedInteger getMethod_num() {
        return method_num;
    }

    public void setMethod_num(UnsignedInteger method_num) {
        this.method_num = method_num;
    }

    public ByteString getParams() {
        return params;
    }

    public void setParams(ByteString params) {
        this.params = params;
    }
}
