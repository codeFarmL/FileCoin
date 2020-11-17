package org.codefarml.filecoin;

import android.util.Base64;
import android.util.Log;

import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.UnsignedInteger;
import ove.crypto.digest.Blake2b;

/**
 * 作者    lgc
 * 时间    2020/11/17 14:50
 * 文件    FileCoin
 * 描述
 */
class Sign {
    private static final String TAG = "Sign";

    private static String priKey = ""; //私钥 hex编码

    /**
     * 通过json生成UnsignedMessageAPI对象
     *
     * @return
     */
    public static UnsignedMessageAPI createUnsignedMessageAPI() {
        UnsignedMessageAPI unsignedMessageAPI = new UnsignedMessageAPI();
        unsignedMessageAPI.setFrom("f1re27enhjrpp7jnr33iioq6am6w6xodu63aodlha");
        unsignedMessageAPI.setTo("f1str7eiaxglndkh5rm7qe4cprlwguwmkoj4xyu5i");
        unsignedMessageAPI.setNonce(30);
        unsignedMessageAPI.setValue("100000000000");
        unsignedMessageAPI.setGasFeeCap("101183");
        unsignedMessageAPI.setGasPremium("100129");
        unsignedMessageAPI.setGas_limit(1000000);
        unsignedMessageAPI.setMethod(0);
        unsignedMessageAPI.setParams("");
        return unsignedMessageAPI;
    }

    /**
     * @param unsignedMessageAPI 签名结构体
     * @param prikey             私钥
     */
    public static void transaction_sign_raw(UnsignedMessageAPI unsignedMessageAPI, String prikey) {
        transaction_sign_secp56k1_raw(unsignedMessageAPI, prikey);
    }

    /**
     * @param unsignedMessageAPI 签名结构体
     * @param _prikey             私钥
     *                           let message_cbor = transaction_serialize(unsigned_message_api)?;
     *                           <p>
     *                           let secret_key = secp256k1::SecretKey::parse_slice(&private_key.0)?;
     *                           <p>
     *                           let cid_hashed = utils::get_digest(message_cbor.as_ref())?;
     *                           <p>
     *                           let message_digest = Message::parse_slice(&cid_hashed)?;
     *                           <p>
     *                           let (signature_rs, recovery_id) = sign(&message_digest, &secret_key);
     *                           <p>
     *                           let mut signature = SignatureSECP256K1 { 0: [0; 65] };
     *                           signature.0[..64].copy_from_slice(&signature_rs.serialize()[..]);
     *                           signature.0[64] = recovery_id.serialize();
     *                           <p>
     *                           Ok(signature)
     */
    public static void transaction_sign_secp56k1_raw(UnsignedMessageAPI unsignedMessageAPI, String _prikey) {
        priKey = _prikey;
        transaction_serialize(unsignedMessageAPI);
    }

    /**
     * @param unsignedMessageAPI
     */
    public static void transaction_serialize(UnsignedMessageAPI unsignedMessageAPI) {
        /**
         * 拼接UnsignedMessage对象
         * 这面用的是CborEncoder
         * 问题：什么是CborEncoder
         */
        UnsignedMessage unsignedMessage = try_from(unsignedMessageAPI);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new CborEncoder(baos).encode(new CborBuilder()
                    .addArray()
                    .add(unsignedMessage.getVersion())
                    // add string
                    .add(unsignedMessage.getTo())
                    .add(unsignedMessage.getFrom())
                    .add(unsignedMessage.getSequence())
                    .add(unsignedMessage.getValue())
                    .add(unsignedMessage.getGas_limit())
                    .add(unsignedMessage.getGasFeeCap())
                    .add(unsignedMessage.getGasPremium())
                    .add(unsignedMessage.getMethod_num())
                    // add integer
                    .add(new co.nstant.in.cbor.model.ByteString(new byte[]{}))
                    .end()
                    .build());
            byte[] encodedBytes = baos.toByteArray();
            byte[] cidHashBytes = getCidHash(encodedBytes);
            sign(cidHashBytes);
        } catch (CborException e) {
            e.printStackTrace();
        }

    }

    /**
     * let value = BigUint::from_str(&message_api.value)?;
     * let gas_limit = message_api.gas_limit;
     * let gas_price = BigUint::from_str(&message_api.gas_price)?;
     * let params = forest_vm::Serialized::new(hex::decode(&message_api.params)?);
     *
     * @param unsignedMessageAPI
     * @return
     */
    public static UnsignedMessage try_from(UnsignedMessageAPI unsignedMessageAPI) {

        //构建交易结构体
        Address from = Address.from_str(unsignedMessageAPI.getFrom());
        Address to = Address.from_str(unsignedMessageAPI.getTo());
        UnsignedMessage unsignedMessage = new UnsignedMessage();
        unsignedMessage.setVersion(new UnsignedInteger(0));
        unsignedMessage.setTo(new co.nstant.in.cbor.model.ByteString(to.getPayload().getSecp256k1().getBytes()));

        unsignedMessage.setFrom(new co.nstant.in.cbor.model.ByteString(from.getPayload().getSecp256k1().getBytes()));

        unsignedMessage.setSequence(new UnsignedInteger(unsignedMessageAPI.getNonce()));
        co.nstant.in.cbor.model.ByteString valueByteString = null;
        if (new BigInteger(unsignedMessageAPI.getValue()).toByteArray()[0] != 0) {
            byte[] byte1 = new byte[new BigInteger(unsignedMessageAPI.getValue()).toByteArray().length + 1];
            byte1[0] = 0;
            System.arraycopy(new BigInteger(unsignedMessageAPI.getValue()).toByteArray(), 0, byte1, 1, new BigInteger(unsignedMessageAPI.getValue()).toByteArray().length);
            valueByteString = new co.nstant.in.cbor.model
                    .ByteString(byte1);
        } else {
            valueByteString = new co.nstant.in.cbor.model
                    .ByteString(new BigInteger(unsignedMessageAPI.getValue()).toByteArray());
        }

        unsignedMessage.setValue(valueByteString);
        unsignedMessage.setGas_limit(new UnsignedInteger(unsignedMessageAPI.getGas_limit()));

        co.nstant.in.cbor.model.ByteString gasFeeCapString = null;
        if (new BigInteger(unsignedMessageAPI.getGasFeeCap()).toByteArray()[0] != 0) {
            byte[] byte2 = new byte[new BigInteger(unsignedMessageAPI.getGasFeeCap()).toByteArray().length + 1];
            byte2[0] = 0;
            System.arraycopy(new BigInteger(unsignedMessageAPI.getGasFeeCap()).toByteArray(), 0, byte2, 1
                    , new BigInteger(unsignedMessageAPI.getGasFeeCap()).toByteArray().length);
            gasFeeCapString = new co.nstant.in.cbor.model
                    .ByteString(byte2);
        } else {
            gasFeeCapString = new co.nstant.in.cbor.model
                    .ByteString(new BigInteger(unsignedMessageAPI.getGasFeeCap()).toByteArray());
        }

        unsignedMessage.setGasFeeCap(gasFeeCapString);


        co.nstant.in.cbor.model.ByteString gasGasPremium = null;
        if (new BigInteger(unsignedMessageAPI.getGasPremium()).toByteArray()[0] != 0) {
            byte[] byte2 = new byte[new BigInteger(unsignedMessageAPI.getGasPremium()).toByteArray().length + 1];
            byte2[0] = 0;
            System.arraycopy(new BigInteger(unsignedMessageAPI.getGasPremium()).toByteArray(), 0, byte2, 1
                    , new BigInteger(unsignedMessageAPI.getGasPremium()).toByteArray().length);
            gasGasPremium = new co.nstant.in.cbor.model
                    .ByteString(byte2);
        } else {
            gasGasPremium = new co.nstant.in.cbor.model
                    .ByteString(new BigInteger(unsignedMessageAPI.getGasPremium()).toByteArray());
        }

        unsignedMessage.setGasPremium(gasGasPremium);


        unsignedMessage.setMethod_num(new UnsignedInteger(0));
        unsignedMessage.setParams(new co.nstant.in.cbor.model.ByteString(new byte[0]));
        return unsignedMessage;
    }

    /**
     * 形成摘要需要拼接的字符串
     */
    public static byte[] CID_PREFIX = new byte[]{0x01, 0x71, (byte) 0xa0, (byte) 0xe4, 0x02, 0x20};

    /**
     * @param message 交易结构体的序列化字节
     *                通过交易结构体字节获取CidHash
     */
    public static byte[] getCidHash(byte[] message) {
        Blake2b.Param param = new Blake2b.Param();
        param.setDigestLength(32);

        //消息体字节
        byte[] messageByte = Blake2b.Digest.newInstance(param).digest(message);

        int xlen = CID_PREFIX.length;
        int ylen = messageByte.length;

        byte[] result = new byte[xlen + ylen];

        System.arraycopy(CID_PREFIX, 0, result, 0, xlen);
        System.arraycopy(messageByte, 0, result, xlen, ylen);

        byte[] prefixByte = Blake2b.Digest.newInstance(param).digest(result);
        String prefixByteHex = NumericUtil.bytesToHex(prefixByte);
        Log.d(TAG, prefixByteHex);

        return prefixByte;


    }

    /**
     * @param cidHash 摘要
     *                对摘要进行椭圆签名椭圆签名
     */
    public static void sign(byte[] cidHash) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(priKey));
        org.web3j.crypto.Sign.SignatureData signatureData = org.web3j.crypto.Sign.signMessage(cidHash,
                ecKeyPair, false);
        byte[] sig = getSignature(signatureData);
        String stringHex = NumericUtil.bytesToHex(sig);
        Log.d(TAG, stringHex);
        String base64 = Base64.encodeToString(sig, Base64.DEFAULT);
        Log.d(TAG, "签名字符串：" + base64);

    }

    /**
     * 获取签名
     *
     * @param signatureData
     * @return
     */
    private static byte[] getSignature(org.web3j.crypto.Sign.SignatureData signatureData) {
        byte[] sig = new byte[65];
        System.arraycopy(signatureData.getR(), 0, sig, 0, 32);
        System.arraycopy(signatureData.getS(), 0, sig, 32, 32);
        sig[64] = (byte) ((signatureData.getV() & 0xFF) - 27);//为啥减去27看signMessage（）方法（内部源码）
        return sig;
    }

}

