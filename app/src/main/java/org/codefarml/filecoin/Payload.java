package org.codefarml.filecoin;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:52
 * 文件    FileCoin
 * 描述
 */
class Payload {
    private Secp256k1 secp256k1;
    /// SECP256K1 key address, 20 byte hash of PublicKey

    public Secp256k1 getSecp256k1() {
        return secp256k1;
    }

    public void setSecp256k1(Secp256k1 secp256k1) {
        this.secp256k1 = secp256k1;
    }
}
