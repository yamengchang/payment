package com.sup.util;

import com.google.gson.Gson;
import com.omv.common.util.basic.ByteUtils;
import com.omv.common.util.basic.MapUtil;
import com.omv.common.util.basic.SerializeUtil;
import com.omv.common.util.basic.ValueUtil;
import com.omv.common.util.signature.Base64;
import com.omv.common.util.signature.DesUtils;
import com.omv.common.util.signature.EncryptManager;
import com.omv.common.util.signature.SignUtils;
import com.sup.pojo.House;
import com.sup.pojo.ReceiveEntity;
import com.sup.pojo.ReturnEntity;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * Created by zwj on 2019/1/9.
 */
public class DataDecodeUtil {

    public static List getData(String instanceInfo, ReceiveEntity receiveEntity) {
        String publicKey = ValueUtil.getFromJson(instanceInfo, "data", "content", "publicKey");
        String privateKey = ValueUtil.getFromJson(instanceInfo, "data", "content", "privateKey");
        EncryptManager encryptManager = new EncryptManager(privateKey, publicKey);
        String mobKey = receiveEntity.getMobKey();
        encryptManager.parseMobKey(mobKey);
        String dataStr = encryptManager.decryptStr(receiveEntity.getData());
        Gson gson  = new Gson();
        List dataList = gson.fromJson(dataStr,List.class);
        return dataList;
    }

    private static String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4W7KA25AvgoMjwnxo+cdNhTASbEGQOQkgulOac1oS4YHL9eb8QFkx++CZffU21UqgIUpAo8qR1wf6/3j8TECnU147671rARINJWLl4Oj4KRInt5FTjBdHC6M6NBQBpQv6xFrtlq8UGORE9LS4HnPoKGoKiIyTPEO18ilsNkHDuwIDAQAB";
    private static String privatekey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALhbsoDbkC+CgyPCfGj5x02FMBJsQZA5CSC6U5pzWhLhgcv15vxAWTH74Jl99TbVSqAhSkCjypHXB/r/ePxMQKdTXjvrvWsBEg0lYuXg6PgpEie3kVOMF0cLozo0FAGlC/rEWu2WrxQY5ET0tLgec+goagqIjJM8Q7XyKWw2QcO7AgMBAAECgYBujibEmWak5wpHdoju1uMK/NTwJ1VF7L4pjzM1ivk4G12f903WdaxHijmNMnjLsiq3Tl9d1htTS/UwMuXVvtCBwWelrTxihNQzcNgS1NNps4fXS6RfhTfGLJLi2tb3AjMBaOgtGosl7GTcJwNewbJr4fv/ElMeH+YSc8Lw79MVOQJBAO/unjJNqjIN/dzcASuLZlV+g7q6GIZp1kqonvySqBJJJtMIpXi7+0uAuxY6id37qz7crqEzRyGuJj8pZnYaf28CQQDEtFLNlVpCkrD2eAfsQYYvxsTNGmFsgmhURO0i8uS4e4ey6JntDh6l75u+a1f6nugh1saoIYDfTPMK7yj979p1AkBRHQ6GWxNK0MgePpJ2si3qgVbvbbKU3nr/ynnVUY9YfzqM5cNrScHvCJo3LZsmXMrL+bdf8AIANOvhNpHZI6QpAkEAlxwYCErV/hKG07C+FWb42LcP9Khxc1RzJVmV+qUxw+9R/cTmis+wB7WcpZn9ClEM7wH5tquWPvT0ONAlY37hCQJAGQ1ITivxyRperaXnHrUcw2b874b0I9jaWpJqh2aGgeHX5KWRQdHJvtEPJVT4lWjXylm5jZb2AGqsgGVK2Bc/tA==";
    public static void main(String[] args) {
        String data = "ea6e0fedd0582704616e2bf6bffff0f075c9192bbe2da585e70772c5f20164c48dc5aca1f1022bbb8faa7d2ed28669c9cec8d0690fd9dda3b4952b145b6589ed097bc6d3feb87b1639dde64e71af1952bc73bfb277c16814d871188f0b7ca357a27b84664a3042ccb9f52048b064ac0b16ae05ff5b8b43ffe524303bff0ebdc3054c193178aaabdbb06247f6b7350c8d298d2121a1be0b53a35432342c9f16e2fcd261b40f25f2755be19970d5a81ccecf05ef7d9177413c36e9014f0188fc53e792b8f6d0a9ddc9cfb9df7b6482fca6b90bfaff9aa6a4141c301be1d2b00fcd47b2164ba7fd270cc112d459c80914b3b729173c5251bba615dd25f88b003c5df3dedd8ba6f1e215dc319815624ee48dcc32ac7bdaf43b2c92d8320e2e362f3a0e9379cc39f75e7e6d1ddf6f0e9e8218026a3cb5a81f0712a1d3add5d14b00de4758034bf1d2543d72df422ea65721f3026e46db13e9f2a780773ed3f26211056037a65685571239621fec7480ee709da1d981733044ef887bfeeee6d7f16e1f52f85c4454b5799d6aa532e394c4b1e8ce75d2652cd85131546f3ff3bea5278449937cebef1d8909af3144429c8164d5c5e785e0ff3a2a518466c582cbf2081f5ad4f011d725154e8785a71faedfc18668526a9def1e92672a8323e14c407a5213f3be15d37836c3f3938f70714fa253052916b9d57d7fb20c8728070274070a488add5e5009eafdf038b779ad4e04533ba725623598c50791865bdf705fa54eccceadbd83ff692cbaf6736a276fd9f72972bd494780a864021d17d81f9019d64e9a6177256c1713e15c4bdc03a114bf4266df2d2a7cdbaaeb8a6ddaf80521e2666befb0b225a3c49bc1d597be9f5540eeedd7b91a54a13dbaa27a06216f3324363c66af2d682f6dc38b5bc7f459e2513a5ab4228b8c1cf61b2e0600258adcc165de17e2e1b8eabeb56f85908e00131cca8ed113f262ace880185d341763b00a52d3988d32fd6e27c005e161e125a268c70d7237abd6c88fc0f037a10b82642cd033045057567f23";
        String mobKey = "L3NouRWOQ5gme6kUomZMfLHW+5BqXlnyTbuYVdmdnw/GdMk2d9UG2fiUJw9if10jAdD62+lueaDApxscaQY9qrFtZK+aqQtFSY11OweWHE0ENRWoxD+58iJGbAUCNzlR2IwgXpHgZ7PE72cuifsWLu3MhPDBNvOHKLx1yIF9lN0=";
        EncryptManager encryptManager = new EncryptManager(privatekey, publickey);
        encryptManager.parseMobKey(mobKey);
        String dataStr = encryptManager.decryptStr(data);
        System.out.println(dataStr);
        Gson gson  = new Gson();
        List dataList = gson.fromJson(dataStr,List.class);
        System.out.println(ValueUtil.toJson(dataList));
        List<ReturnEntity> returnEntityList = new ArrayList<>(dataList.size());

        for (Object obj : dataList) {
            House house = (House) MapUtil.mapToObject( (Map) obj,House.class);
            System.out.println(ValueUtil.toJson(house));
            returnEntityList.add(ReturnEntity.success(house.getId(), house.getThirdNo()));
        }
        System.out.println(ValueUtil.toJson(returnEntityList));
    }

//    //测试des
//    public static void main(String[] args) throws Exception {
//        String enc = DesUtils.encryptByKey("123","12345678");
//        System.out.println(enc);
////        String dec = DesUtils.decryptByKey(enc,"12345678");
//        String dec = DesUtils.decryptByKey("6u1n1q7jQ5A=","12345678");
//        System.out.println(dec);
//    }


//    public static void main(String[] args)throws Exception {
//        String preKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALB6kT8ALer3W2d9+WwiU607vb9UEa0CQJ3AsdvmhtEbrN+Ak8pNB2r1EZW3NOCAyjr5+q0sSWsSKyIzacBh1AJAyRcVGPHk+n9WPhbENwChjsSytyoDhlsqQ1rXljxBj3+7Qhju64h6Cvz8qKoCMrKpSQ5d34Oe8nj8UWgp401JAgMBAAECgYA0KZZC49+uorxeo6qUnrTS8GSOZ9/tnkvRFQK9N69DoYWAbnXAhvIZWCSBdyxm60pbEkZY500wG6eqLT4lwgSywpxJ8tkELGQxLM/YaBTqw/OddH/PjsPvWqA23/I8Ojmth1nkkDax4voSo9fiL8T3C5gC0L3O6RZhKDRw5euVAQJBAPhX64BV9hWMcAlnExhHLMlkXeFmgN+7k1tdsvzIMbdYLfKT6Dxw4bG6tCBD1a8ncRevM19SlqECTf1D5l+/rOkCQQC163J0aQZI7frGttTWHemV+gpOhDbRJfQc4AGIqxIAKrcKrHkrmNJK7JqqsjhAmy1DHNSFnoU2AhyOnpY6uuFhAkB+JOKJkKBr96bdD89CML0OGv4YxOFHQ9covKJzBhlCwPat1fVL5iauCWD/VpWO36DxX9vfWmsKo8oLkFbPYeQpAkAK5/+nnPhEfQfwy9s8rvRnCmN9y41hwo29AWz2HZNpsSXiHO7yvym0VjT7kM2wid0PzfWrwRIpZ70Ai99jy/phAkBymq5aOsYpgbatv7W+RKvMDqaJzKCQacL3AoBxDvRGGqxWQVn0B9AL7vYfwoEuac6RDD017l46gKetfOYAJHGf";
//        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwepE/AC3q91tnfflsIlOtO72/VBGtAkCdwLHb5obRG6zfgJPKTQdq9RGVtzTggMo6+fqtLElrEisiM2nAYdQCQMkXFRjx5Pp/Vj4WxDcAoY7EsrcqA4ZbKkNa15Y8QY9/u0IY7uuIegr8/KiqAjKyqUkOXd+DnvJ4/FFoKeNNSQIDAQAB";
//        Map<String,String> azfParams = new HashMap<>();
//        azfParams.put("instanceId","I204351081976591011");
//        azfParams.put("busiName","上海家旬信息科技有限公司");
//        azfParams.put("contactName","王征");
//        azfParams.put("provinceId","28");
//        azfParams.put("cityId","249");
//        azfParams.put("address","上海市奉贤区环城东路323号北2楼201室");
//        azfParams.put("merNameAlias","爱租房");
//        azfParams.put("servicePhone","021-67181311");
//        azfParams.put("contactEmail","qiuzhe1120@163.com");
//        azfParams.put("contactPhone","15821005397");
//        azfParams.put("cerdId","230102197703213436");
//        azfParams.put("cardNoCipher","'31001903408052533943");
//        azfParams.put("cardName","上海家旬信息科技有限公司");
//        azfParams.put("isCompay","1");
//        azfParams.put("number","10000101");
//        azfParams.put("accBankNo","01050000");
//        SignUtils.getSign(azfParams,preKey);
//        System.out.println(URLEncoder.encode(azfParams.get("sign")));
//
//        System.out.println(SignUtils.verifySign(azfParams,pubKey));
//        System.out.println(URLDecoder.decode("dGSCXnTubhRijkFwZufj0w5QYQ7yUuenL7Pm9xwmYHAn19wOlWRtQQqgyRnWwFegSXTwEt7aojM2HUN87CtjJAyRXYTnC5wcIAQse0YpSQ2QYgfy8oo/FBasviKC4kqYQ+GWhMFRIbtsruS3nmCHarBBccUinxDw6J2aKcZPKfc="));
//
//    }


    //测试验签
//    public static void main(String[] args) {
//        String data = "{\"data\":\"6f6a7442476d6243305a6730665476564770394549662b386a7030414b7374733764414e4b6433397565622b375a516b4862775637437364456a2b702f3041493372664973366655666d6c4347513844364c685141437949346b4a396839477a64352b4838783539786b38794544373764347245716578367a474c704254426156366476776e616e54776671306d34796f6e68716b66664f68676c48666673566337362f6c375669664145474231512f443151656142384666304a6547577a436847435a58704d774e76476a4438786870567257464174625442684b48704a52494567524e7568694a654d6e66495248465a386649585a452f746a7a4551576a3376366947626b68565546576878446179757a36667050566969743078393032744c67524c34686f706d4b427531666869766c5a2f6b74496651415568375756464379744f6f6867586578656753635a374d6b517454484356724c54716543446b4e374e61584b6265676c2b414a7163626d72726d6b3856695364356f445642637263445858517271436c7a324e4f4a5466483473497077746767625649696f50703451436267316d494158537258354475684773546e44746765477259547343317158737250776848664b5448417a2b7653592f5871454a4364745246784241374f384639636c7a55706f5a697337307059776266575152725245462b39384747695139364138724362355464676b596b4134636d42666b314b4d674a61593663614930482b3667457441724d4436667842356e38787a34686a67757048435248476a6b3131566e686e357756327347543752736c65565575314537516e525a574c773838416b696c6a4c67416f366f2b566f4f424873492b4f6d7a7633594776636e72305768634a413055575536397045454b6f654c71502b396c593035374357317665786c784b2f2f543335682b453132703147634b6c4249413944327551447170682b6641493831544f502f384d6c575069394c7946314d6144556f704b6c576a52376e44414c483763725a416a31594d486267544251622f4a7778444e424e765a742b6c65706578766478412b3378317342497273536b67614d434e4f574e326f59576a6f775574386267334769664c2b6b4964524446613151416669723466553153386e6536664b4d4750586b6a5566507a683131673249594c4f70763639367634415742504a7776523573656f4a43395779455a337633526e797652576d65357376756d33696f2f52613736686e7a6275674f36534f66514458755037434d6a5168745459332f4d6f6e6c74426c2f7a6e7577335a4d70794a7865705971496e6a6c6a6e394432514d6178553236416945386a4331594a7145\",\"instanceId\":\"I000000000000000001\",\"mobKey\":\"Y0cLrWi22XSW3zssS9EOpTlJwuOMHw8hrY0J7hxMg9pI14NtKWcHEtuRqr+Iw6eTvFqHls74qWFsKm4jdjw1stVzwdr27r\\/+b\\/h5A\\/uAlT7tT3fVL\\/StxplDXvXIQ2Mj8gD4wDvF9nCSpEqZEW44WE676R7pQL1lVq2LkdFlLCY=\",\"notifyUrl\":\"www.baidu.com\",\"timestamp\":\"1553656001\",\"sign\":\"UGBwIKO7E2R0C3xXJl0wXyE3vcuyOgym4+VBn7w+rE9tIXRK3JzPDNibqfVX4PWLvnQX3hBmNubeWmP+SI\\/cMlTcajyPML7\\/luMwFOiMe\\/7z7EUcsHzRZNn\\/krWgtTezC+Y8FnTAMbcmoA6EeehA5EBHgkKJ58cdPHRkrgIp+wA=\"}";
//        Map<String,Object> bodyParams = MapUtil.str2Map(data);
//        Map<String, String> reqMap = new LinkedHashMap();
//        String instanceId = null;
//        for (Map.Entry<String, Object> entry : bodyParams.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (key.equals("instanceId")) {
//                instanceId = (String) value;
//            }
//            reqMap.put(key, value.toString());
//        }
//
//        if (!SignUtils.verifySign(reqMap, publickey)) {
//            ValueUtil.isError("验签失败");
//        }
//    }
}
