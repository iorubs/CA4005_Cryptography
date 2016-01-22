import java.util.Random;
import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.security.MessageDigest;

class Assi2
{
    static String modulos = "b59dd79568817b4b9f6789822d22594f376e6a9abc0241846de426e5dd8f6eddef00b465f38f509b2b18351064704fe75f012fa346c5e2c442d7c99eac79b2bc8a202c98327b96816cb8042698ed3734643c4c05164e739cb72fba24f6156b6f47a7300ef778c378ea301e1141a6b25d48f1924268c62ee8dd3134745cdf7323";
    static String generator = "44ec9d52c8f9189e49cd7c70253c2eb3154dd4f08467a64a0267c9defe4119f2e373388cfa350a4e66e432d638ccdc58eb703e31d4c84e50398f9f91677e88641a2d2f6157e2f4ec538088dcf5940b053c622e53bab0b4e84b1465f5738f549664bd7430961d3e5a2e7bceb62418db747386a58ff267a9939833beefb7a6fd68";

    private static BigInteger getSecretKey(BigInteger p) {
        Random rand = new Random();

        BigInteger diff = BigInteger.valueOf(4);
        BigInteger max = p.subtract(diff);

        int bitLen = max.bitLength();

        BigInteger random;

        do
        {
            random = new BigInteger(bitLen, rand);
        } while (random.compareTo(max) > 0); 
        
        diff = BigInteger.valueOf(2);
        return random.add(diff);
    }

    private static BigInteger gcd(BigInteger a, BigInteger b)
    {
        if (b.equals(BigInteger.ZERO))
            return a;

        return gcd(b, a.mod(b));
    }

    private static BigInteger getRandK(BigInteger p) {
        Random rand = new Random();

        BigInteger diff = BigInteger.valueOf(3);
        BigInteger max = p.subtract(diff);

        int bitLen = max.bitLength();

        BigInteger random;

        do
        {
            random = new BigInteger(bitLen, rand);
        } while (random.compareTo(max) > 0);

        random = random.add(BigInteger.ONE);

        if(gcd(random, p.subtract(BigInteger.ONE)).equals(BigInteger.ONE))
            return random;
        else
            return getRandK(p);

    }

    private static byte[] readFileToByteArray(String file_name)
    {
        try
        {
            Path file_path = Paths.get(file_name);
            return Files.readAllBytes(file_path);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    static private BigInteger sha256(byte[] file)
    {
        BigInteger hashed_file = BigInteger.ONE;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hashed_file  = new BigInteger(md.digest(file));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return hashed_file;
    }

    private static BigInteger[] extEuclidean(BigInteger a, BigInteger p)
    { 
        BigInteger[] ans = new BigInteger[3];

        if (p.equals(BigInteger.ZERO))
        {
            ans[0] = a;
            ans[1] = BigInteger.ONE;
            ans[2] = BigInteger.ZERO;
        }
        else
        {
           ans = extEuclidean (p, a.mod(p));
           BigInteger temp = ans[1].subtract(ans[2].multiply(a.divide(p)));
           ans[1] = ans[2];
           ans[2] = temp;
        }

        return ans;
    }

    private static BigInteger modInverse(BigInteger a, BigInteger p)
    {//where a^-1 (mod p)

        BigInteger[] ans = extEuclidean(a, p);

        if(!ans[0].equals(BigInteger.ONE))
            return BigInteger.ZERO;

        return ans[1].mod(p);
    }

    private static BigInteger getS(BigInteger p, BigInteger x, BigInteger r, BigInteger k, BigInteger m)
    {
        /*(H(m)-xr) = tmp*/
        BigInteger tmp = m.subtract(x.multiply(r));

        return modInverse(k, p.subtract(BigInteger.ONE)).multiply(tmp);
    }

    static private String byteToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (byte b : bytes)
        {
            if(count == 4)
            {
                sb.append(String.format(" %02X", b));
                count = 0;
            }
            else
                sb.append(String.format("%02X", b));

            count++;
        }
        
        return sb.toString();
    }

    public static void main(String [] args)
    {
        BigInteger p = new BigInteger(modulos, 16);
        BigInteger g = new BigInteger(generator, 16);

        BigInteger y = BigInteger.ZERO;
        BigInteger r = BigInteger.ZERO;

        byte[] file = readFileToByteArray("Assi2.zip");
        BigInteger m = sha256(file);

        BigInteger s = BigInteger.ZERO;

        while(s.equals(BigInteger.ZERO))
        {
            BigInteger x = getSecretKey(p);
            y = g.modPow(x, p);
            BigInteger k = getRandK(p);
            r = g.modPow(k, p);
            s = getS(p, x, r, k, m);
        }

        System.out.println("Public Key Y:");
        System.out.println(byteToHex(y.toByteArray()));
        System.out.println();
        System.out.println("Value S:");
        System.out.println(byteToHex(s.toByteArray()));
        System.out.println();
        System.out.println("Value R:");
        System.out.println(byteToHex(r.toByteArray()));
        System.out.println();
    }
}