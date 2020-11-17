package org.codefarml.filecoin;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:51
 * 文件    FileCoin
 * 描述
 */
class Address {
    private String network = "f";
    private Payload payload;  //应该是字节 截取20位

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static Address from_str(String addressStr) {
        Address address = new Address();
        //去掉前两位
        String str = addressStr.substring(2);

        byte[] bytes12 = new byte[21];

        //为啥加1，因为是Secp256k1的标识就是1
        bytes12[0] = 1;
        System.arraycopy(Base32New.decode(str), 0, bytes12, 1, 20);
        Secp256k1 secp256k1 = new Secp256k1();
        secp256k1.setBytes(bytes12);
        Payload payload = new Payload();
        payload.setSecp256k1(secp256k1);
        address.setPayload(payload);
        return address;
    }
}
