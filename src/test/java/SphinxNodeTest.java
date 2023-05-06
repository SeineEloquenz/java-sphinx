import com.robertsoultanaev.javasphinx.*;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class SphinxNodeTest {
    @Test
    public void processSphinxPacket() {
        final var node = new SphinxNode(new SphinxParams());

        BigInteger secret = new BigInteger("8594556911718241073939018500914787396871958538713354284467465626596");

        byte[] encodedEcPoint = Hex.decode("0360071b99894f0e9fe6ebc9f5a4ca1763b2aaf965278ea3aa90758a42");
        ECPoint inputAlpha = Util.decodeECPoint(encodedEcPoint);

        byte[] inputBeta = Hex.decode("abb7447c35df0c5eb1145077aee0174a0c3f9dcaecd02fd5d027729a2e634b9453c94ad7e34cbdc0e9a6e50349de99761cff056eee32c871f93b3ac9999991732bc3275a39ac685ac1e711876f89cf04d5c5cc150d1abb9bb88beb7df24156be905e8758aad72cae2c567ee7c0dc112c81bc8394482496cd8a5bfb83bd05014107d00e1c7efff71d16bd707f48297f344eecfa72774d44dcab5c612568934ca9");

        byte[] inputGamma = Hex.decode("b52c05c57698fe727bdc26b7ea51243c");

        byte[] inputDelta = Hex.decode("3fc07ece7060a2c6253f2ff2399992f6376d19db83e1645995ae25e2fe3d1e2e25beb1ef93a12992cbef340e5d734742768f06287a9403b506e2215200af1ffd1a5b1a689253963fba67433ee5102638fb0855e95aefab43a9efcffe1d3c21504e7c48aa8af2b19f1ed6f77165ef2a599af9763348531357b32c460d375837995fa9f2ec3a4747818d6ada998b762d874e7412dc297400988c4f72a0ebfedef8e827238c5e17d19587eaae77e6a02d10956b505cf592a5b46a7a74eb86664afa3d9a43fa9e09281cb4ca27ee55fc7a7dde626fdaedca48167ca19a77763d2d94a8991bbd044fa60de46a87a9aff144b6cea541f7e092af23324dd5199b91d87db95c4716774379b2c548afed3b8c33d3d3bffe5057105938614fb7638b62490a59b6117d7db094841e7fbb22cf0949ba6addc6c3f4161cc3aa8c1641f9267a71e2324329bc90c092db052cd03434cba98af7281ceb203bd827e14ab6b40fc09359167b3169b0b32b518fa4bd04e29f338cab6cd995bab577c93ebcf2d41add100109a3ba49a6ef636680f460c3d08c56a7b4d3e80bc65df54bdb41de0cfe3a5025f35ad56c600aa992cd2671de4754d6e2f52d060f68b25caebd2a793970219b238c22efd0b6c6d9b65a5bc13a2e3253fa190d164452fb15c0d5133c03f7565b838e35830bdbb0b79d1dad2a3b4f2ca0382f233aaf592bccf2ed77ff21a13f31f58858cb6ee7e7d625b397127cf3ac7d0302b7d607cbe547a7dd71fcef988e86374ebcdca79cc945a8763aefca6d947e804eaa69f64fbdabb7c1e9bb3191506b2d4fe18b525d7b407e3589231230775df782c5c78f79a9bce0fb81042af89f490d22e3f9cd7202be4f25beb5b7321d85fe0800e472c239ac481a5d479db1a0bd1119f58126e43046d3db3103be50c5062c06deb6abd6839242634c0200f6ce3ab20f1e56f238d1bb02a10586340353eea11d685882c1bd036fb3aa2b912781b4b138d2eab0f44aafc3ac33484f80b1e2ff3ec52ce466ec48426f6142a0f030cd01ea315c231342006f71a40a98c3a6169ad65a04f43c81688c971e19f7a2fa91657aba2e261b6d766e95e59a24c63713282f50d656dbc3fdfa4dbdde7e794f04a295de61271d7b441aebb5824095c7df91d30f84067878d4c077d41f14da18a4fe66de05cc2ff753b8016358ba391eca6d104bb771283d60d2501f1cbe586282e5155657744bf510b789c59e9a1feb723eb3c9c28f7afc69dcd40f37ccd1f26ce568b314e0841ccde9f18c1f6f5fc28366d46da904ac5ea96dd1d5c5d979824473fda27a0ab443ca764af15385dd731dbe36bcf9bcc97b9f781863f87869c622e9c4315aae954a60570df92df667d6005db357829461112a9d16ff23e608b31c7a736293396b96e20210539c205a8225785b1602ceb436eb9439e65a3e71236d");

        Header inputHeader = new Header(inputAlpha, inputBeta, inputGamma);

        HeaderAndDelta inputHeaderAndDelta = new HeaderAndDelta(inputHeader, inputDelta);

        byte[] expectedTag = Hex.decode("77ba738155f9c416a39edb8df6aad043");
        byte[] expectedRouting = Hex.decode("92a2c3b0c40101");

        encodedEcPoint = Hex.decode("03b1b56e289a274ad157c54090f1a92b3af27afd6187dbb000813f50e3");
        ECPoint expectedAlpha = Util.decodeECPoint(encodedEcPoint);

        byte[] expectedBeta = Hex.decode("d7ec470907fcc7972a0f7801cf33ab7faafc3fabbcf7c2bf24a2a1bdfde66f52b67fd0cf6bf50cd13fc4384e5ab85e6e5b9433077f5c79e02de8369692619007c10dce0f3afc3a0f00127bdcf4284112d301e83691af7af7155b131dd8cacb5c5e3bbe8d9e6a8d33c48eef2bfb3641da4c23ba7068e98b5cd90f5e754822637cf09e5d53b1440fadd14c684385d8f4b6272f7007b8945898a63e9b33fd4d7e35");

        byte[] expectedGamma = Hex.decode("f8800d0c22fdd0d9f75cfa6e48d6dce9");

        byte[] expectedDelta = Hex.decode("fd6ca6dfae13d4dc32af0e964ac54147b0ad27aa6830d5a93138b10a7aa7b4425205a0063a808e195cc98b1783ce9c85b30648dad1f70ec6c66433af65d61ca9829f0f70d3a728f37256d021c7617ea2af52433e569e80b89581865db2d993d3e2d1df2d900808bca5f396a09b7f4100085220b63989a587216cfeabb1a6a48f2789781fc573f881c6032934e371c5c84a0e2c020f4926c7de8a79a644015705a8a610ba584df1f0e4247bcdb00b9555210555c814c663184134aaa55d82b380c97b5721cca28fff887986ed3a27dcbece6ca9dd93fef94362bc619fef656c161fa2a4bbf5301f60d96834e3efaf50e766606755606af90220826b89dfab3a788485887173999022850f4d30ff6d6d15ddbef71e80200bdfaf180ccd5a584688b03ffd1fda22613ff95c20de4b23ef63bed082499768bee96fe68d52ef957309192e52e04fd2f8b6e637a29c22aa326cc9cadcd3a071e4c69ff4db5cfe8569214a6285b8ca4f75d4c6909f1776fe3467a3bc6542103e5d4a8f20cdde218448ca077449397bbb311707996b4045af0879c951af7033a77837dcd88cc11f0093f402a6e3011cd2af5d162242a196ac7e5720f050bd92bf3b2637a4df3003e6984b4faf643776194cb6fd2c6d9f67c4e4b34ef92a192f79c9b9027d61c359d732a10b886f23a6a69885fd04be633975fc45826a761abac530e989b9d4a10ddef4cdbf07b554ce2c99d7905e1cf20d74ac1e344af9b63dfd9411d9430b708d58531665b38393c950df90c855a703a78bbcaa68d5b31034e5046add6136977a5c0ed403690a25b951c1f347a50b84b24c0234ad782cf00efc8001deb6e78e27528ced16cca7239f41b4e9f78478804421337945b6fec8697860607b021abfdb319e81c36b9d2b490d6cc719cf796fa18c4b07350e9323d72c52d611445cfebc66e791b07ff9d125277e60ddf0fbaec9d81fade646d70fe6acef1f555aa3ec2f95291cd9049d7ed82adafdbb04da097f5eee6e06c67d8a4d6dd8b9b12904f638e441dd1ae7e183f08475481857a88a88eb0f9908be932e10623c9b71725498358c8d9430afe59cdf88652e4c33388ba7ab41ac220c67f1e33d02d0f8e34bf9644c6f60bb6f1e67f3c96d1df8f4aaaedfcb099a7550b92aefb232a7154ff81e5a1377c50d41cd3e96a734596ba166286a73814fe23fe8ab52237e98b98b1cd1f0d3ab075fa4b9fd1f5bfb23adc71e6ce1d1e10cf2142af731153293849ab7558c26aa56bc7cf430be4e763a09fa27bb9e867141d8de73d9d225ae69a266e3d0ce309a96fe2214e478af06701f2afc8aab4914af56ce84466083d05ba742eb6de17d064fcc048ef4071a4a8bac0e00b4246adc8732ebfd8c8338e9cbd3791cacf4c22952fcff9f82a300538ac86a5ed638b10f55607222143c80504bde1e32c051ea99ff");

        ProcessedPacket output = node.sphinxProcess(secret, inputHeaderAndDelta);
        byte[] outputTag = output.tag();
        byte[] outputRouting = output.routing();
        HeaderAndDelta outputHeaderAndDelta = output.headerAndDelta();
        Header outputHeader = outputHeaderAndDelta.header();
        byte[] outputDelta = outputHeaderAndDelta.delta();

        assertArrayEquals(expectedTag, outputTag);
        assertArrayEquals(expectedRouting, outputRouting);
        assertEquals(expectedAlpha, outputHeader.alpha());
        assertArrayEquals(expectedBeta, outputHeader.beta());
        assertArrayEquals(expectedGamma, outputHeader.gamma());
        assertArrayEquals(expectedDelta, outputDelta);
    }

    @Test(expected = SphinxException.class)
    public void processSphinxPacketBadMac() {
        final var node = new SphinxNode(new SphinxParams());

        BigInteger secret = new BigInteger("8594556911718241073939018500914787396871958538713354284467465626596");

        byte[] encodedEcPoint = Hex.decode("0360071b99894f0e9fe6ebc9f5a4ca1763b2aaf965278ea3aa90758a42");
        ECPoint inputAlpha = Util.decodeECPoint(encodedEcPoint);

        byte[] inputBeta = Hex.decode("abb7447c35df0c5eb1145077aee0174a0c3f9dcaecd02fd5d027729a2e634b9453c94ad7e34cbdc0e9a6e50349de99761cff056eee32c871f93b3ac9999991732bc3275a39ac685ac1e711876f89cf04d5c5cc150d1abb9bb88beb7df24156be905e8758aad72cae2c567ee7c0dc112c81bc8394482496cd8a5bfb83bd05014107d00e1c7efff71d16bd707f48297f344eecfa72774d44dcab5c612568934ca9");

        byte[] badGamma = Hex.decode("ffffffffffffffffffffffffffffffff");

        byte[] inputDelta = Hex.decode("3fc07ece7060a2c6253f2ff2399992f6376d19db83e1645995ae25e2fe3d1e2e25beb1ef93a12992cbef340e5d734742768f06287a9403b506e2215200af1ffd1a5b1a689253963fba67433ee5102638fb0855e95aefab43a9efcffe1d3c21504e7c48aa8af2b19f1ed6f77165ef2a599af9763348531357b32c460d375837995fa9f2ec3a4747818d6ada998b762d874e7412dc297400988c4f72a0ebfedef8e827238c5e17d19587eaae77e6a02d10956b505cf592a5b46a7a74eb86664afa3d9a43fa9e09281cb4ca27ee55fc7a7dde626fdaedca48167ca19a77763d2d94a8991bbd044fa60de46a87a9aff144b6cea541f7e092af23324dd5199b91d87db95c4716774379b2c548afed3b8c33d3d3bffe5057105938614fb7638b62490a59b6117d7db094841e7fbb22cf0949ba6addc6c3f4161cc3aa8c1641f9267a71e2324329bc90c092db052cd03434cba98af7281ceb203bd827e14ab6b40fc09359167b3169b0b32b518fa4bd04e29f338cab6cd995bab577c93ebcf2d41add100109a3ba49a6ef636680f460c3d08c56a7b4d3e80bc65df54bdb41de0cfe3a5025f35ad56c600aa992cd2671de4754d6e2f52d060f68b25caebd2a793970219b238c22efd0b6c6d9b65a5bc13a2e3253fa190d164452fb15c0d5133c03f7565b838e35830bdbb0b79d1dad2a3b4f2ca0382f233aaf592bccf2ed77ff21a13f31f58858cb6ee7e7d625b397127cf3ac7d0302b7d607cbe547a7dd71fcef988e86374ebcdca79cc945a8763aefca6d947e804eaa69f64fbdabb7c1e9bb3191506b2d4fe18b525d7b407e3589231230775df782c5c78f79a9bce0fb81042af89f490d22e3f9cd7202be4f25beb5b7321d85fe0800e472c239ac481a5d479db1a0bd1119f58126e43046d3db3103be50c5062c06deb6abd6839242634c0200f6ce3ab20f1e56f238d1bb02a10586340353eea11d685882c1bd036fb3aa2b912781b4b138d2eab0f44aafc3ac33484f80b1e2ff3ec52ce466ec48426f6142a0f030cd01ea315c231342006f71a40a98c3a6169ad65a04f43c81688c971e19f7a2fa91657aba2e261b6d766e95e59a24c63713282f50d656dbc3fdfa4dbdde7e794f04a295de61271d7b441aebb5824095c7df91d30f84067878d4c077d41f14da18a4fe66de05cc2ff753b8016358ba391eca6d104bb771283d60d2501f1cbe586282e5155657744bf510b789c59e9a1feb723eb3c9c28f7afc69dcd40f37ccd1f26ce568b314e0841ccde9f18c1f6f5fc28366d46da904ac5ea96dd1d5c5d979824473fda27a0ab443ca764af15385dd731dbe36bcf9bcc97b9f781863f87869c622e9c4315aae954a60570df92df667d6005db357829461112a9d16ff23e608b31c7a736293396b96e20210539c205a8225785b1602ceb436eb9439e65a3e71236d");

        Header inputHeader = new Header(inputAlpha, inputBeta, badGamma);

        HeaderAndDelta inputHeaderAndDelta = new HeaderAndDelta(inputHeader, inputDelta);

        node.sphinxProcess(secret, inputHeaderAndDelta);
    }



    @Test(expected = SphinxException.class)
    public void processSphinxPacketBadBetaLength() {
        BigInteger secret = new BigInteger("8594556911718241073939018500914787396871958538713354284467465626596");

        byte[] encodedEcPoint = Hex.decode("0360071b99894f0e9fe6ebc9f5a4ca1763b2aaf965278ea3aa90758a42");
        ECPoint inputAlpha = Util.decodeECPoint(encodedEcPoint);

        byte[] inputBeta = Hex.decode("abb7447c35df0c5eb1145077aee0174a0c3f9dcaecd02fd5d027729a2e634b9453c94ad7e34cbdc0e9a6e50349de99761cff056eee32c871f93b3ac9999991732bc3275a39ac685ac1e711876f89cf04d5c5cc150d1abb9bb88beb7df24156be905e8758aad72cae2c567ee7c0dc112c81bc8394482496cd8a5bfb83bd05014107d00e1c7efff71d16bd707f48297f344eecfa72774d44dcab5c612568934ca9");

        byte[] badGamma = Hex.decode("ffffffffffffffffffffffffffffffff");

        byte[] inputDelta = Hex.decode("3fc07ece7060a2c6253f2ff2399992f6376d19db83e1645995ae25e2fe3d1e2e25beb1ef93a12992cbef340e5d734742768f06287a9403b506e2215200af1ffd1a5b1a689253963fba67433ee5102638fb0855e95aefab43a9efcffe1d3c21504e7c48aa8af2b19f1ed6f77165ef2a599af9763348531357b32c460d375837995fa9f2ec3a4747818d6ada998b762d874e7412dc297400988c4f72a0ebfedef8e827238c5e17d19587eaae77e6a02d10956b505cf592a5b46a7a74eb86664afa3d9a43fa9e09281cb4ca27ee55fc7a7dde626fdaedca48167ca19a77763d2d94a8991bbd044fa60de46a87a9aff144b6cea541f7e092af23324dd5199b91d87db95c4716774379b2c548afed3b8c33d3d3bffe5057105938614fb7638b62490a59b6117d7db094841e7fbb22cf0949ba6addc6c3f4161cc3aa8c1641f9267a71e2324329bc90c092db052cd03434cba98af7281ceb203bd827e14ab6b40fc09359167b3169b0b32b518fa4bd04e29f338cab6cd995bab577c93ebcf2d41add100109a3ba49a6ef636680f460c3d08c56a7b4d3e80bc65df54bdb41de0cfe3a5025f35ad56c600aa992cd2671de4754d6e2f52d060f68b25caebd2a793970219b238c22efd0b6c6d9b65a5bc13a2e3253fa190d164452fb15c0d5133c03f7565b838e35830bdbb0b79d1dad2a3b4f2ca0382f233aaf592bccf2ed77ff21a13f31f58858cb6ee7e7d625b397127cf3ac7d0302b7d607cbe547a7dd71fcef988e86374ebcdca79cc945a8763aefca6d947e804eaa69f64fbdabb7c1e9bb3191506b2d4fe18b525d7b407e3589231230775df782c5c78f79a9bce0fb81042af89f490d22e3f9cd7202be4f25beb5b7321d85fe0800e472c239ac481a5d479db1a0bd1119f58126e43046d3db3103be50c5062c06deb6abd6839242634c0200f6ce3ab20f1e56f238d1bb02a10586340353eea11d685882c1bd036fb3aa2b912781b4b138d2eab0f44aafc3ac33484f80b1e2ff3ec52ce466ec48426f6142a0f030cd01ea315c231342006f71a40a98c3a6169ad65a04f43c81688c971e19f7a2fa91657aba2e261b6d766e95e59a24c63713282f50d656dbc3fdfa4dbdde7e794f04a295de61271d7b441aebb5824095c7df91d30f84067878d4c077d41f14da18a4fe66de05cc2ff753b8016358ba391eca6d104bb771283d60d2501f1cbe586282e5155657744bf510b789c59e9a1feb723eb3c9c28f7afc69dcd40f37ccd1f26ce568b314e0841ccde9f18c1f6f5fc28366d46da904ac5ea96dd1d5c5d979824473fda27a0ab443ca764af15385dd731dbe36bcf9bcc97b9f781863f87869c622e9c4315aae954a60570df92df667d6005db357829461112a9d16ff23e608b31c7a736293396b96e20210539c205a8225785b1602ceb436eb9439e65a3e71236d");

        Header inputHeader = new Header(inputAlpha, inputBeta, badGamma);

        HeaderAndDelta inputHeaderAndDelta = new HeaderAndDelta(inputHeader, inputDelta);

        final var badParams = new SphinxParams() {
            @Override
            public int getHeaderLength() {
                return 0;
            }
        };
        final var node = new SphinxNode(badParams);

        node.sphinxProcess(secret, inputHeaderAndDelta);
    }
}
